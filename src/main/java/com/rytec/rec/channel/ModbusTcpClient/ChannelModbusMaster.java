package com.rytec.rec.channel.ModbusTcpClient;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.Channel;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.util.AnnotationChannelType;
import com.rytec.rec.util.AnnotationJSExport;
import com.serotonin.modbus4j.ModbusFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;


/**
 * 使用客户端的方式与接入的Tcp Modbus 通讯，不用写自己的通讯控制
 * 1、每个Modbus总线对应一个ModbusMaster对象，
 * 2、一个ModbusMaster对应一个Session，
 * 3、每个发送的命令都是 ModbusRequest
 */

@Service
@Order(400)
@AnnotationChannelType(1002)
@AnnotationJSExport("Modbus 客户端")
public class ChannelModbusMaster extends RecBase implements ChannelInterface, ManageableInterface {

    static ModbusFactory modbusFactory;

    @Autowired
    private DbConfig dbConfig;

    @Autowired
    ChannelManager channelManager;

    @Autowired
    public NodeManager nodeManager;

    @Autowired
    AsyncRunner asyncRunner;

    public volatile boolean inLoop = false;      // 是否进行循环

    // 客户端对象
    // <ChannelId: Session>
    public HashMap<Integer, RecModbusMasterSession> modbusClients = new HashMap();

    /*
     * 两级 HashMap，保存Channel下面的Node
     * 第一级：channelId->Map
     * 第二级：nodeId->ChannelNode
     */
    public HashMap<Integer, HashMap> channelNodes = new HashMap();

    private void getConfig() {
        // 初始化两级的Hash，保存每个Chanel下面的Node
        // ------------------ 初始化ChannelNode 的HashMap -----------------
        //
        List<ChannelNode> chaNodeList = dbConfig.getChannelNodeList();

        //第一级的Map ChannelId-> map
        for (ChannelNode cn : chaNodeList) {
            if (cn.getCtype() != 1002) {        //只管理该类型的Channel
                continue;
            }

            //是否已经存在该Channel
            HashMap<Integer, ChannelNode> cha = channelNodes.get(cn.getId());

            //不存在，建立该Channel
            if (cha == null) {
                cha = new HashMap();
                this.channelNodes.put(cn.getId(), cha);
            }

            //node的标识是 nid
            cha.put(cn.getNid(), cn);
        }


        // ----------------- 初始化Client的配置，这里只是建立一个列表，并没有进行连接
        List<Channel> channels = dbConfig.getChannelList();
        RecModbusMasterSession session;

        for (Channel channel : channels) {
            if (channel.getType() == 1002) {
                session = new RecModbusMasterSession(this);
                session.ip = channel.getIp();
                session.port = channel.getPort();
                session.channelId = channel.getId();
                session.intTimeQueue(channelNodes);                 // 初始化轮询命令
                modbusClients.put(channel.getId(), session);
            }
        }

    }

    @Override
    @PreDestroy
    public void stop() {
        stopLoop();
        for (RecModbusMasterSession session : modbusClients.values()) {
            if (session.modbusMaster != null) {
                session.modbusMaster.destroy();
                session.modbusMaster = null;
            }
        }

        modbusClients.clear();
        channelNodes.clear();
    }

    /**
     * 开启服务器
     * 根据配置建立TCP连接
     */
    @Override
    public void start() {
        stop();
        getConfig();                                    // 得到配置
        if (modbusFactory == null) {
            modbusFactory = new ModbusFactory();
        }

        startLoop();
    }

    @Override
    public int sendMsg(Object msg) {
        return 0;
    }

    @Override
    public void channelOnline(Object channelId, boolean online) {
        HashMap<Integer, ChannelNode> channelNodeMap = channelNodes.get(channelId);
        if (channelNodeMap == null) {
            return;
        }

        if (channelNodeMap.values().size() > 0) {
            ChannelNode cn = (ChannelNode) channelNodeMap.values().toArray()[0];

            if (online) {
            } else {
                channelManager.channelOffline(cn.getId());
            }

        }
    }

    /**
     * 定时任务，检测连接和发送命令
     */
    public void startLoop() {
        inLoop = true;
        asyncRunner.runLoop();
        logger.debug("--------------结束循环------------------");
    }

    /**
     * 停止线程循环
     */
    private void stopLoop() {
        inLoop = false;
    }
}

package com.rytec.rec.channel.Modbus;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.channel.Modbus.Client.ModbusClient;
import com.rytec.rec.db.model.Channel;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.util.AnnotationChannelType;
import com.rytec.rec.util.AnnotationJSExport;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Modbus Clent Channel 管理
 */
//@Service
//@Order(400)
//@AnnotationChannelType(1002)
//@AnnotationJSExport("Modbus 客户端")
public class ChannelModbusClient extends ChannelModbusBase implements ManageableInterface {

    // 客户端连接列表
    public final ConcurrentHashMap<Integer, ModbusClient> clients = new ConcurrentHashMap<>();


    /*
     * 两级 HashMap，保存Channel下面的Node
     * 第一级：channelId->Map
     * 第二级：nodeId->ChannelNode
     */
    public HashMap<Integer, HashMap> channelNodes = new HashMap();

    // Channel 配置
    List<Channel> channels = dbConfig.getChannelList();

    @Override
    public void stop() {
        for (ModbusClient client : clients.values()) {
            // 断开客户端连接
            client.close();
        }
        clients.clear();
        channelNodes.clear();
    }

    @Override
    public void start() {
        stop();

        for (Channel channel : channels) {
            if (channel.getType() != 1002) {
                continue;
            }

            // 连接客户端，并加入到客户端列表
            clients.put(channel.getId(), new ModbusClient(this, channel.getId(), channel.getIp(), channel.getPort()));
        }
    }

    /**
     * 读取Channel和Node的配置
     */
    private void getConfig() {
        // 初始化两级的Hash，保存每个Chanel下面的Node
        // ------------------ 初始化ChannelNode 的HashMap -----------------
        //
        List<ChannelNode> chaNodeList = dbConfig.getChannelNodeList();

        //第一级的Map ChannelId-> map
        for (ChannelNode cn : chaNodeList) {
            if (cn.getCtype() != 1002) {
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
    }


    @Override
    public void addClient(Object key, Object client) {
        this.clients.put((Integer) key, (ModbusClient) client);
    }

    @Override
    public void delClient(Object key) {
        this.clients.remove(key);
    }

    @Override
    public List<ChannelNode> getChannelNodes(Object key) {
        return new ArrayList<ChannelNode>(channelNodes.get(key).values());
    }

    @Scheduled(fixedDelay = 100)
    private void doOnTime() {
        // 遍历已经登录的远端，并执行队列
        for (ModbusClient client : clients.values()) {
            if (client.channelFuture.channel().isWritable()) {
                ModbusTcpSession modbusTcpSession = client.channelFuture.channel().attr(ModbusCommon.MODBUS_STATE).get();
                if (modbusTcpSession != null) {
                    modbusTcpSession.timerProcess();
                }
            }
        }
    }
}

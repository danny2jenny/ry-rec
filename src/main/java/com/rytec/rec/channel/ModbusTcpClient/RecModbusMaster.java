package com.rytec.rec.channel.ModbusTcpClient;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.util.AnnotationChannelType;
import com.rytec.rec.util.AnnotationJSExport;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.WriteCoilRequest;
import com.serotonin.modbus4j.msg.WriteCoilResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


/**
 * 使用客户端的方式与接入的Tcp Modbus 通讯，不用写自己的通讯控制
 */

//@Service
//@Order(400)
//@AnnotationChannelType(1002)
//@AnnotationJSExport("Modbus Master")
public class RecModbusMaster extends RecBase implements ChannelInterface, ManageableInterface {

    static ModbusFactory modbusFactory;

    @Autowired
    private DbConfig dbConfig;

    @Autowired
    ChannelManager channelManager;

    @Autowired
    public NodeManager nodeManager;

    // 对应的客远程的连接列表
    // ip:port->ModbusMaster
    public final HashMap<String, ModbusMaster> clients = new HashMap<>();

    /*
     * 两级 HashMap
     * 第一级：ip:port->Map
     * 第二级：nodeId->ChannelNode
     */
    public HashMap<String, HashMap> channelNodes = new HashMap();


    /*
     * 初始化对应的HashMap
     * 两级 HashMap
     * 第一级：ip:port->Map
     * 第二级：nodeId->ChannelNode
     */
    private void getConfig() {

        List<ChannelNode> chaNodeList = dbConfig.getChannelNodeList();

        //第一级的Map ip:id-> map
        for (ChannelNode cn : chaNodeList) {
            if (cn.getCtype() != 1002) {        //只管理该类型的Channel
                continue;
            }

            //每一个Channel的标识是IP：PORT
            String chaId = cn.getIp() + ':' + cn.getPort();

            //是否已经存在该Channel
            HashMap<Integer, ChannelNode> cha = channelNodes.get(chaId);

            //不存在，建立该Channel
            if (cha == null) {
                cha = new HashMap();
                this.channelNodes.put(chaId, cha);
            }

            //node的标识是 nid
            cha.put(cn.getNid(), cn);
        }
    }

    static {
        if (modbusFactory == null) {
            modbusFactory = new ModbusFactory();
        }
    }

    /**
     * 获取tcpMaster
     *
     * @return
     * @throws ModbusInitException
     */
    public static ModbusMaster getMaster() throws ModbusInitException {
        IpParameters params = new IpParameters();
        params.setHost("127.12.34.56");
        params.setPort(8234);
        params.setEncapsulated(true);

        ModbusMaster tcpMaster = modbusFactory.createTcpMaster(params, false);
        tcpMaster.init();

        return tcpMaster;
    }

    /**
     * 写 [01 Coil Status(0x)]写一个 function ID = 5
     *
     * @param slaveId     slave的ID
     * @param writeOffset 位置
     * @param writeValue  值
     * @return 是否写入成功
     * @throws ModbusTransportException
     * @throws ModbusInitException
     */
    public static boolean writeCoil(int slaveId, int writeOffset, boolean writeValue)
            throws ModbusTransportException, ModbusInitException {
        // 获取master
        ModbusMaster tcpMaster = getMaster();
        // 创建请求
        WriteCoilRequest request = new WriteCoilRequest(slaveId, writeOffset, writeValue);

        // 发送请求并获取响应对象
        WriteCoilResponse response = (WriteCoilResponse) tcpMaster.send(request);
        if (response.isException()) {
            return false;
        } else {
            return true;
        }
    }

    public void textModbus() {
        try {

            boolean t01 = writeCoil(1, 0, true);

            //@formatter:on
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }

    /**
     * 开启服务器
     * 根据配置建立TCP连接
     */
    @Override
    public void start() {
        stop();

        getConfig();            // 得到配置

        for (String ip: clients.keySet()){

        }

    }

    @Override
    public int sendMsg(Object msg) {
        return 0;
    }

    @Override
    public void channelOnline(Object channelId, boolean online) {

    }
}

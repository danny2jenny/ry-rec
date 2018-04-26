package com.rytec.rec.channel.ModbusTcpClient;

import com.rytec.rec.app.RecBase;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.NodeRuntimeBean;
import com.rytec.rec.node.modbus.cfg.ModbusNodeCfg;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantFromWhere;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.*;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 每个 Modbus Master 客户端对应的Session
 */
public class RecModbusMasterSession extends RecBase {

    public String ip;
    public int port;
    public int channelId = 0;

    public ModbusMaster modbusMaster;

    private ChannelModbusMaster channelModbusMaster;

    // 实时命令队列
    private Queue<ModbusClientMsg> instantQueueCmd = new ConcurrentLinkedQueue();

    // 定时查询命令队列
    // 节点类型:节点地址 -> ModbusClientMsg
    private volatile HashMap<String, ModbusClientMsg> timerQueryList = new HashMap();
    private volatile List<String> timerQueryListIndex = new ArrayList();

    private volatile int currentTimerQuerryIndex = 0;


    /**
     * 构造函数
     *
     * @param mm modbusClient 管理对象
     */
    public RecModbusMasterSession(ChannelModbusMaster mm) {
        this.channelModbusMaster = mm;
    }

    /**
     * 初始化定时命令，把同一个485地址的，相同类别的节点归为一个查询
     */
    public void intTimeQueue(HashMap<Integer, HashMap> channelNodes) {

        // 只过滤本通道的节点
        HashMap<Integer, ChannelNode> cha = channelNodes.get(channelId);

        if (cha == null) {
            return;
        }

        for (ChannelNode cn : cha.values()) {
            NodeInterface iNode = channelModbusMaster.nodeManager.getNodeComInterface(cn.getNtype());

            // 类型一致，地址一致组织成为一个读取
            if (iNode != null) {
                String key = "" + cn.getNtype() + ':' + cn.getAdr();
                if (timerQueryList.get(key) == null) {
                    timerQueryListIndex.add(key);
                    // 生成命名
                    ModbusClientMsg msg = new ModbusClientMsg();
                    ModbusNodeCfg cfg = (ModbusNodeCfg) iNode.getCfg();

                    msg.type = ConstantCommandType.GENERAL_READ;
                    msg.from = ConstantFromWhere.FROM_TIMER;
                    msg.node = cn.getNid();
                    msg.modbusAddr = cn.getAdr();
                    msg.modbusMsg = cfg.modbusCmd;
                    msg.modbusOffset = cfg.regOffset;
                    msg.modbusCount = cfg.regCount;

                    timerQueryList.put(key, msg);
                }

            }
        }
    }

    /**
     * 得到下一个的查询命令
     */
    private ModbusClientMsg getNextQuery() {
        if (timerQueryList.size() == 0) {
            return null;
        }
        ModbusClientMsg msg = timerQueryList.get(timerQueryListIndex.get(currentTimerQuerryIndex));
        currentTimerQuerryIndex = (currentTimerQuerryIndex + 1) % timerQueryList.size();
        return msg;
    }

    /**
     * 开始通讯
     * 1、检查连接
     * 2、发送命令
     */
    @Async
    public void startCom() {
        // 检查是否连接
        if (modbusMaster == null) {
            // 连接Slave
            IpParameters params = new IpParameters();
            params.setHost(ip);
            params.setPort(port);
            params.setEncapsulated(true);

            modbusMaster = channelModbusMaster.modbusFactory.createTcpMaster(params, true);

            try {
                // 连接成功
                modbusMaster.init();
                channelModbusMaster.channelOnline(channelId, true);
            } catch (ModbusInitException e) {
                // 连接失败
                modbusMaster = null;
                channelModbusMaster.channelOnline(channelId, false);
            }
        }
        // 连接不成功，退出
        if (modbusMaster == null) {
            return;
        }

        // 首先执行及时命令
        ModbusClientMsg outMsg = instantQueueCmd.poll();

        // 再执行定时命令
        if (outMsg == null) {
            outMsg = getNextQuery();
        }

        if (outMsg == null) {
            return;
        }

        sendMsg(outMsg);
    }

    /**
     * 发送命令，并立即获得返回，这是同步的方式
     *
     * @param msg
     */
    private void sendMsg(ModbusClientMsg msg) {
        ModbusResponse response;

        short[] rstInt;                         // 返回的数据，整数，未处理
        float[] rstFloat = new float[4];        // 浮点，已经处理

        try {
            response = modbusMaster.send(msg.genReadRequest());
            // todo 读取返回，进行解析

            NodeRuntimeBean nodeRunTime = channelModbusMaster.nodeManager.getChannelNodeByNodeId(msg.node);

            if (response instanceof ReadCoilsResponse) {
                // 1

            } else if (response instanceof ReadDiscreteInputsResponse) {
                // 2
            } else if (response instanceof ReadHoldingRegistersResponse) {
                // 3
                rstInt = ((ReadHoldingRegistersResponse) response).getShortData();

                // 转换成浮点数据
                rstFloat[0] = nodeRunTime.nodeConfig.pA * rstInt[0];
                rstFloat[1] = nodeRunTime.nodeConfig.pA * rstInt[1];
                rstFloat[2] = nodeRunTime.nodeConfig.pA * rstInt[2];
                rstFloat[3] = nodeRunTime.nodeConfig.pA * rstInt[3];

                // 组建 NodeMessage 发送给 NodeManager 进行处理
                NodeMessage nodeMessage = new NodeMessage();
                nodeMessage.from = msg.from;
                nodeMessage.node = msg.node;
                nodeMessage.type = msg.type;
                nodeMessage.value = rstFloat;
                channelModbusMaster.nodeManager.onMessage(nodeMessage);

            } else if (response instanceof ReadInputRegistersResponse) {
                // 4
                rstInt = ((ReadInputRegistersResponse) response).getShortData();

                // 转换成浮点数据
                rstFloat[0] = nodeRunTime.nodeConfig.pA * rstInt[0];
                rstFloat[1] = nodeRunTime.nodeConfig.pA * rstInt[1];
                rstFloat[2] = nodeRunTime.nodeConfig.pA * rstInt[2];
                rstFloat[3] = nodeRunTime.nodeConfig.pA * rstInt[3];

                // 组建 NodeMessage 发送给 NodeManager 进行处理
                NodeMessage nodeMessage = new NodeMessage();
                nodeMessage.from = msg.from;
                nodeMessage.node = msg.node;
                nodeMessage.type = msg.type;
                nodeMessage.value = rstFloat;
                channelModbusMaster.nodeManager.onMessage(nodeMessage);

            } else if (response instanceof WriteCoilResponse) {
                // 5
            } else if (response instanceof WriteRegisterResponse) {
                // 6
            }
        } catch (ModbusTransportException e) {
            //e.printStackTrace();
            // 断开Socket，
            modbusMaster.destroy();
            modbusMaster = null;
            // 更新状态
        }
    }
}

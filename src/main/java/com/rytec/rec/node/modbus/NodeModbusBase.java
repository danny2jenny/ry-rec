package com.rytec.rec.node.modbus;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;
import com.rytec.rec.channel.ModbusTcpServer.ModbusMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.NodeRuntimeBean;
import com.rytec.rec.node.base.BaseNode;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantErrorCode;
import com.rytec.rec.util.ConstantFromWhere;
import com.rytec.rec.util.ConstantModbusCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * Created by Danny on 2017/2/23.
 * 的基础类型
 *
 * 子类需要初始化
 *
 * modbuCmd
 * regCount
 * regOffset
 */
public abstract class NodeModbusBase extends BaseNode {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 各个实现需要设置该值
     */
    public int modbusCmd = 1;  // Modbus的命令，1\2\3\4\5
    public int regCount = 1;   // 寄存器的数量
    public int regOffset = 0;  // 寄存器偏移量


    @Autowired
    NodeManager nodeManager;

    @Autowired
    ChannelManager channelManager;


    public Object genMessage(int where, int nodeId, int cmd, int value) {

        ChannelNode cn = nodeManager.getChannelNodeByNodeId(nodeId).channelNode;

        ModbusMessage frame = new ModbusMessage();

        frame.from = where;
        frame.nodeId = nodeId;
        frame.type = cmd;

        switch (modbusCmd) {
            case ConstantModbusCommand.READ_WRITE_COILS:                  // 999
                switch (cmd) {
                    case ConstantCommandType.GENERAL_READ:
                        frame.payload = ModbusFrame.readCoils(cn.getAdr(), regOffset, regCount);
                        frame.responseLen = 5 + (int) Math.ceil(((double) regCount) / 8);
                        break;
                    case ConstantCommandType.GENERAL_WRITE:
                        frame.payload = ModbusFrame.writeCoil(cn.getAdr(), cn.getNo(), value);
                        frame.responseLen = 8;
                        break;
                }
                break;
            case ConstantModbusCommand.READ_INPUT:                  // 2
                frame.payload = ModbusFrame.readInput(cn.getAdr(), regOffset, regCount);
                frame.responseLen = 5 + (int) Math.ceil(((double) regCount) / 8);
                break;
            case ConstantModbusCommand.READ_HOLDING_REGISTERS:      // 3
                frame.payload = ModbusFrame.readHoldingRegisters(cn.getAdr(), regOffset, regCount);
                frame.responseLen = 5 + regCount * 2;
                break;
            case ConstantModbusCommand.READ_REGISTERS:              // 4
                frame.payload = ModbusFrame.readRegisters(cn.getAdr(), regOffset, regCount);
                frame.responseLen = 5 + regCount * 2;
                break;
        }

        return frame;
    }


    //消息解码
    public void decodeMessage(Object msg) {

        ModbusMessage modbusMessage = (ModbusMessage) msg;

        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = modbusMessage.from;
        nodeMsg.type = modbusMessage.type;
        nodeMsg.node = modbusMessage.nodeId;

        ByteBuf payload = modbusMessage.payload;
        ByteBuf data = Unpooled.buffer();

        int modbusCmd = payload.getByte(1);

        switch (modbusCmd) {
            case ConstantModbusCommand.READ_COILS:                  // 1
                payload.getBytes(3, data, (int) Math.ceil(((double) regCount) / 8));
                processIO(nodeMsg, data);
                break;
            case ConstantModbusCommand.READ_INPUT:                  // 2
                payload.getBytes(3, data, (int) Math.ceil(((double) regCount) / 8));
                processIO(nodeMsg, data);
                break;
            case ConstantModbusCommand.READ_HOLDING_REGISTERS:      // 3
                payload.getBytes(3, data, regCount * 2);
                processAnalog(nodeMsg, data);
                break;
            case ConstantModbusCommand.READ_REGISTERS:              // 4
                payload.getBytes(3, data, regCount * 2);
                processAnalog(nodeMsg, data);
                break;
            case ConstantModbusCommand.WRITE_COIL:                  // 5
                payload.getBytes(4, data, 2);
                processSwitch(nodeMsg, data);
                break;
        }

        payload.release();

    }

    /**
     * 处理开关控制返回
     *
     * @param msg
     * @param data
     */
    private void processSwitch(NodeMessage msg, ByteBuf data) {
        int val = data.getByte(1);
        if (val > 0) {
            msg.value = true;
        } else msg.value = false;

        data.release();
        nodeManager.onMessage(msg);
    }

    /**
     * 处理IO数据
     *
     * @param msg
     * @param data
     */
    private void processIO(NodeMessage msg, ByteBuf data) {
        NodeRuntimeBean nodeRunTime = nodeManager.getChannelNodeByNodeId(msg.node);
        int channelId = nodeRunTime.channelNode.getId();
        HashMap<Integer, ChannelNode> nodes = (HashMap<Integer, ChannelNode>) channelManager.channelNodes.get(channelId);

        int byteVal = 0;
        int adrMask;
        int byteNumber = 0;
        for (ChannelNode node : nodes.values()) {

            // 485 地址是一致的，类型是一致的
            if ((nodeRunTime.channelNode.getAdr().intValue() == node.getAdr().intValue()) &&
                    (nodeRunTime.channelNode.getNtype().intValue() == node.getNtype().intValue())) {

                byteNumber = node.getNo() / 8;
                byteVal = data.getByte(byteNumber);

                //是一个地址的设备，位操作后
                adrMask = 0x01 << (node.getNo() % 8);
                if ((adrMask & byteVal) > 0) {
                    msg.value = true;
                } else {
                    msg.value = false;
                }

                // 判断是否需要反向
                if (nodeRunTime.nodeConfig.pA < 0) {
                    msg.value = !((Boolean) msg.value);
                }

                msg.node = node.getNid();
                nodeManager.onMessage(msg);
            }
        }
        data.release();
    }

    /**
     * 处理模拟数据
     *
     * @param msg
     * @param data
     */
    private void processAnalog(NodeMessage msg, ByteBuf data) {
        NodeRuntimeBean nodeRunTime = nodeManager.getChannelNodeByNodeId(msg.node);
        int channelId = nodeRunTime.channelNode.getId();
        HashMap<Integer, ChannelNode> nodes = (HashMap<Integer, ChannelNode>) channelManager.channelNodes.get(channelId);

        NodeRuntimeBean currentNodeRuntime;

        int analogVal = 0;
        int valueIndex = 0;
        for (ChannelNode node : nodes.values()) {

            // 485 地址是一致的，类型是一致的
            if ((nodeRunTime.channelNode.getAdr().intValue() == node.getAdr().intValue()) &&
                    (nodeRunTime.channelNode.getNtype().intValue() == node.getNtype().intValue())) {

                valueIndex = node.getNo() * 2;
                analogVal = data.getShort(valueIndex);
                currentNodeRuntime = nodeManager.getChannelNodeByNodeId(node.getNid());
                msg.value = currentNodeRuntime.nodeConfig.pA * analogVal + currentNodeRuntime.nodeConfig.pB;


                msg.node = node.getNid();
                nodeManager.onMessage(msg);
            }
        }
        data.release();

    }

    /**
     * 一个Modbus设备的健康状况
     *
     * @param msg ModbusMessage
     * @param h
     */
    public void goodHelth(Object msg, boolean h) {

        if (h) {
            return;
        }

        ModbusMessage modbusMessage = (ModbusMessage) msg;
        // 得到Node的Channel
        NodeRuntimeBean nodeRunTime = nodeManager.getChannelNodeByNodeId(modbusMessage.nodeId);
        // 得到所有Channel下的Node
        HashMap<Integer, ChannelNode> channelNodes = (HashMap<Integer, ChannelNode>) channelManager.channelNodes.get(nodeRunTime.channelNode.getId());

        NodeMessage nodeMessage = new NodeMessage();
        nodeMessage.from = ConstantFromWhere.FROM_SYSTEM;
        nodeMessage.value = null;

        // 该地址下的所有Node
        for (ChannelNode node : channelNodes.values()) {
            if ((nodeRunTime.channelNode.getAdr().intValue() == node.getAdr().intValue()) &&
                    (nodeRunTime.channelNode.getNtype().intValue() == node.getNtype().intValue())) {
                nodeMessage.node = node.getNid();
                nodeManager.onMessage(nodeMessage);
            }
        }

    }

    public int sendMessage(NodeMessage msg) {
        // todo: 完善错误处理
        int rst = 0;

        //找到对应的 Channel
        ChannelNode channelNode = nodeManager.getChannelNodeByNodeId(msg.node).channelNode;

        ChannelInterface channel = channelManager.getChannelInterface(channelNode.getCtype());


        ModbusMessage outMsg = null;

        switch (msg.type) {
            case ConstantCommandType.GENERAL_READ:
                break;
            case ConstantCommandType.GENERAL_WRITE:
                if (msg.value instanceof Boolean) {
                    if ((Boolean) msg.value) {
                        outMsg = (ModbusMessage) genMessage(msg.from, msg.node, msg.type, 0xFF00);
                    } else {
                        outMsg = (ModbusMessage) genMessage(msg.from, msg.node, msg.type, 0x0000);
                    }
                } else {
                    return ConstantErrorCode.NODE_VALUE_TYPE;
                }
                break;
        }

        channel.sendMsg(outMsg);

        return rst;
    }

}

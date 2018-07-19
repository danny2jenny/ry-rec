package com.rytec.rec.node.modbus.base;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.channel.Modbus.ModbusFrame;
import com.rytec.rec.channel.Modbus.ModbusMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.NodeRuntimeBean;
import com.rytec.rec.node.modbus.cfg.ModbusNodeCfg;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantErrorCode;
import com.rytec.rec.util.ConstantFromWhere;
import com.rytec.rec.util.ConstantModbusCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * Created by Danny on 2017/2/23.
 * 的基础类型
 * <p>
 * 子类需要初始化
 * <p>
 * modbuCmd
 * regCount
 * regOffset
 */
public abstract class DmaModbusBase extends BaseModbusNode {


    @Autowired
    public NodeManager nodeManager;

    @Autowired
    public ChannelManager channelManager;


    /**
     * @param where  从哪里来的真 1 系统 2 联动 3 用户
     * @param nodeId node 的ID
     * @param cmd    命令  对应 util/ConstantCommandType
     * @param value  值
     * @return
     */
    @Override
    public Object genMessage(int where, int nodeId, int cmd, int regCount, int value) {

        ChannelNode cn = nodeManager.getChannelNodeByNodeId(nodeId).channelNode;

        ModbusMessage frame = new ModbusMessage();

        frame.from = where;
        frame.nodeId = nodeId;
        frame.type = cmd;
        frame.regCount = regCount;

        switch (modbusCmd) {
            case ConstantModbusCommand.READ_WRITE_COILS:                  // 999
                switch (cmd) {
                    case ConstantCommandType.GENERAL_READ:
                        frame.payload = ModbusFrame.readCoils(cn.getAdr(), regOffset, regCount);
                        frame.responseLen = 5 + (int) Math.ceil(((double) regCount) / 8);
                        break;
                    case ConstantCommandType.GENERAL_WRITE:
                        frame.payload = ModbusFrame.writeCoil(cn.getAdr(), cn.getNo() - 1, value);
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
            case ConstantModbusCommand.WRITE_REGISTER:              // 6
                frame.payload = ModbusFrame.writeRegister(cn.getAdr(), regOffset, value);
                frame.responseLen = 8;
                break;
        }

        return frame;
    }


    /**
     * 消息解码，然后不同的类型返回，不同的处理
     *
     * @param msg
     */
    @Override
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
                payload.getBytes(3, data, payload.readableBytes() - 5);
                processIO(nodeMsg, data);
                break;
            case ConstantModbusCommand.READ_INPUT:                  // 2
                payload.getBytes(3, data, payload.readableBytes() - 5);
                processIO(nodeMsg, data);
                break;
            case ConstantModbusCommand.READ_HOLDING_REGISTERS:      // 3
                payload.getBytes(3, data, payload.readableBytes() - 5);
                processAnalog(nodeMsg, data);
                break;
            case ConstantModbusCommand.READ_REGISTERS:              // 4
                payload.getBytes(3, data, payload.readableBytes() - 5);
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

                byteNumber = (node.getNo() - 1) / 8;
                byteVal = data.getByte(byteNumber);

                //是一个地址的设备，位操作后
                adrMask = 0x01 << ((node.getNo() - 1) % 8);
                if ((adrMask & byteVal) > 0) {
                    msg.value = true;
                } else {
                    msg.value = false;
                }

                // 判断是否需要反向

                if (nodeManager.getChannelNodeByNodeId(node.getNid()).nodeConfig.pA < 0) {
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

                valueIndex = (node.getNo() - 1) * 2;
                analogVal = data.getShort(valueIndex);
                currentNodeRuntime = nodeManager.getChannelNodeByNodeId(node.getNid());
                msg.value = currentNodeRuntime.nodeConfig.pA * analogVal + currentNodeRuntime.nodeConfig.pB;

                // 对负值进行处理
                if ((Float) msg.value < 0) {
                    msg.value = new Float(0);
                }

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
    @Override
    public void goodHelth(Object msg, boolean h) {

        if (h) {
            return;
        }

        // 如果不健康，把Value设置成null，然后发送给Node
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

    @Override
    public int sendMessage(NodeMessage msg) {
        // todo: 完善错误处理
        int rst = 0;

        //找到对应的 Channel
        ChannelNode channelNode = nodeManager.getChannelNodeByNodeId(msg.node).channelNode;

        ChannelInterface channel = channelManager.getChannelInterface(channelNode.getCtype());


        ModbusMessage outMsg = null;

        NodeRuntimeBean nodeRuntimeBean = nodeManager.getChannelNodeByNodeId(msg.node);
        switch (msg.type) {
            case ConstantCommandType.GENERAL_READ:
                break;
            case ConstantCommandType.GENERAL_WRITE:

                if (msg.value instanceof Boolean) {
                    // 开关量发送
                    if ((Boolean) msg.value) {
                        // 打开
                        if (nodeRuntimeBean.nodeConfig.pA < 0) {
                            outMsg = (ModbusMessage) genMessage(msg.from, msg.node, msg.type, 0, 0x0000);
                        } else {
                            outMsg = (ModbusMessage) genMessage(msg.from, msg.node, msg.type, 0, 0xFF00);
                        }
                    } else {
                        // 关闭
                        if (nodeRuntimeBean.nodeConfig.pA < 0) {
                            outMsg = (ModbusMessage) genMessage(msg.from, msg.node, msg.type, 0, 0xFF00);
                        } else {
                            outMsg = (ModbusMessage) genMessage(msg.from, msg.node, msg.type, 0, 0x0000);
                        }

                    }
                } else if (msg.value instanceof Integer) {
                    // 整形量发送

                } else {
                    return ConstantErrorCode.NODE_VALUE_TYPE;
                }
                break;
        }

        channel.sendMsg(outMsg);

        return rst;
    }

    /**
     * @return
     */
    @Override
    public Object getCfg() {
        ModbusNodeCfg cfg = new ModbusNodeCfg();
        cfg.modbusCmd = modbusCmd;
        cfg.regOffset = regOffset;

        return cfg;
    }

}

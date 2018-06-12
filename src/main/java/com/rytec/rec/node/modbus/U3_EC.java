package com.rytec.rec.node.modbus;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;
import com.rytec.rec.channel.ModbusTcpServer.ModbusMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.modbus.base.DmaModbusBase;
import com.rytec.rec.util.*;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * * 状态地址：0x0801，(READ_HOLDING_REGISTERS=3)
 * 0：停止
 * 1：工作
 * 控制：WRITE_REGISTER = 6
 */
@Service
@AnnotationNodeType(5001)
@AnnotationJSExport("U3-EC 工业空调")
public class U3_EC extends DmaModbusBase {

    @Override
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return oldVal != newVal;
    }

    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_HOLDING_REGISTERS;
        regOffset = 0x0801;
    }

    /**
     * 需要重载命令生成
     *
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

        switch (cmd) {
            case ConstantCommandType.GENERAL_READ:
                // 读取状态
                frame.payload = ModbusFrame.readHoldingRegisters(cn.getAdr(), 0x0801, 1);
                frame.responseLen = 7;
                break;
            case ConstantCommandType.GENERAL_WRITE:
                /**
                 * 控制，根据Value来写入不同的寄存器
                 */
                switch (value) {
                    case ConstantAircon.STATE_STOP:
                        // 停止
                        frame.payload = ModbusFrame.writeRegister(cn.getAdr(), 0x0801, 0);
                        break;
                    case ConstantAircon.STATE_COLD:
                        // 制冷
                        frame.payload = ModbusFrame.writeRegister(cn.getAdr(), 0x0801, 1);
                        break;
                    case ConstantAircon.STATE_HOT:
                        // 制热
                        frame.payload = ModbusFrame.writeRegister(cn.getAdr(), 0x0801, 1);
                        break;
                }
                frame.responseLen = 8;
                break;
        }
        return frame;
    }

    /**
     * 消息解码
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

        int modbusCmd = payload.getByte(1);

        switch (modbusCmd) {
            case ConstantModbusCommand.READ_HOLDING_REGISTERS:      // 3
                // 读取状态
                nodeMsg.value = payload.getShort(3);
                nodeManager.onMessage(nodeMsg);
                break;
        }

        payload.release();
    }

    /**
     * 发送命令
     *
     * @param msg
     * @return
     */
    @Override
    public int sendMessage(NodeMessage msg) {
        int rst = 0;

        //找到对应的 Channel
        ChannelNode channelNode = nodeManager.getChannelNodeByNodeId(msg.node).channelNode;
        ChannelInterface channel = channelManager.getChannelInterface(channelNode.getCtype());
        ModbusMessage outMsg;

        outMsg = (ModbusMessage) genMessage(msg.from, msg.node, msg.type, 0, (Integer) msg.value);
        channel.sendMsg(outMsg);

        return rst;
    }

}

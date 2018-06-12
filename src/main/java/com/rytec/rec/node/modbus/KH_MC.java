package com.rytec.rec.node.modbus;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;
import com.rytec.rec.channel.ModbusTcpServer.ModbusMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.ValueCompare;
import com.rytec.rec.node.modbus.base.DmaModbusBase;
import com.rytec.rec.util.*;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Service;

/**
 * 山东科华的井盖
 * <p>
 * 状态地址：3100
 * 长度:3
 * 1：版本号
 * 2：0、正常；1、倾斜
 * 3:1、开启；2、关闭；3、表示进入保护状态 不可以操作推杆；4、表示推杆运动中；5、表示解除保护状态，可以操作推杆
 * <p>
 * 控制地址：3300
 * 1、开启
 * 2、关闭
 */
@Service
@AnnotationNodeType(1101)
@AnnotationJSExport("科华-井盖")
public class KH_MC extends DmaModbusBase {

    @Override
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return ValueCompare.booleanNeedUpdate(cfg, oldVal, newVal);
    }

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

        switch (cmd) {
            case ConstantCommandType.GENERAL_READ:
                // 读取状态
                frame.payload = ModbusFrame.readHoldingRegisters(cn.getAdr(), 3000, 3);
                frame.responseLen = 11;
                break;
            case ConstantCommandType.GENERAL_WRITE:
                // 控制，根据Value来写入不同的寄存器
                frame.payload = ModbusFrame.writeRegister(cn.getAdr(), 3300, value);
                frame.responseLen = 8;
                break;
        }

        return frame;
    }

    /**
     * Decode message
     *
     * @param msg
     */
    @Override
    public void decodeMessage(Object msg) {
        ModbusMessage modbusMessage = (ModbusMessage) msg;

        // Create a exchange NodeMessage for nodeManager
        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = modbusMessage.from;
        nodeMsg.type = modbusMessage.type;
        nodeMsg.node = modbusMessage.nodeId;

        ByteBuf payload = modbusMessage.payload;

        int modbusCmd = payload.getByte(1);

        switch (modbusCmd) {
            case ConstantModbusCommand.READ_HOLDING_REGISTERS:      // 3
                // 读取状态
                int v = payload.getShort(7);

                if (v == 1) {
                    nodeMsg.value = true;
                } else {
                    nodeMsg.value = false;
                }

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

        if (msg.value instanceof Boolean) {
            if ((Boolean) msg.value) {
                // Switch ON
                outMsg = (ModbusMessage) genMessage(msg.from, msg.node, msg.type, 0, 1);
            } else {
                // Switch OFF
                outMsg = (ModbusMessage) genMessage(msg.from, msg.node, msg.type, 0, 2);
            }

            channel.sendMsg(outMsg);
        }

        return rst;
    }


}

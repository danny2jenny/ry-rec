package com.rytec.rec.node.modbus;


import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.Modbus.ModbusFrame;
import com.rytec.rec.channel.Modbus.ModbusMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.modbus.base.DmaBaseModbus;
import com.rytec.rec.util.*;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 有温湿度的功能，直接使用 RS_WSD
 * 注意，用户设置的时候，以下端口需要加1
 * 状态地址：215，(READ_HOLDING_REGISTERS=3)
 * 0：停止
 * 1：制冷
 * 2：制热
 * 控制：WRITE_REGISTER = 6
 * 185：制冷
 * 186：制热
 * 187：关机
 */
@Service
@AnnotationNodeType(2004)
@AnnotationJSExport("RS-KTC 空调")
public class RS_KTC extends DmaBaseModbus {
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
        regOffset = 185;
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
                frame.payload = ModbusFrame.readHoldingRegisters(cn.getAdr(), 215, 1);
                frame.responseLen = 7;
                break;
            case ConstantCommandType.GENERAL_WRITE:
                /**
                 * 控制，根据Value来写入不同的寄存器
                 */
                switch (value) {
                    case ConstantAircon.STATE_STOP:
                        // 停止
                        frame.payload = ModbusFrame.writeRegister(cn.getAdr(), 187, 1);
                        break;
                    case ConstantAircon.STATE_COLD:
                        // 制冷
                        frame.payload = ModbusFrame.writeRegister(cn.getAdr(), 185, 1);
                        break;
                    case ConstantAircon.STATE_HOT:
                        // 制热
                        frame.payload = ModbusFrame.writeRegister(cn.getAdr(), 186, 1);
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

        int val;
        int modbusCmd = payload.getByte(1);

        switch (modbusCmd) {
            case ConstantModbusCommand.READ_HOLDING_REGISTERS:      // 3
                // 读取状态
                nodeMsg.value = payload.getShort(3);
                nodeManager.onMessage(nodeMsg);
                break;
            case ConstantModbusCommand.WRITE_REGISTER:              // 6
                // 控制
                // 控制返回可以不做处理
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

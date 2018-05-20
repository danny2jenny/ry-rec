package com.rytec.rec.node.modbus;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;
import com.rytec.rec.channel.ModbusTcpServer.ModbusMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantModbusCommand;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 地址：0为广播地址
 * 功能号：06
 * 地址：0x0134  308
 * 数据：两个byte
 * 00 06 01 34 0C 0C CD 2C  发送
 * 07 06 01 34 0C 0C CC 9B  返回
 */
@Service
@AnnotationNodeType(1201)
@AnnotationJSExport("LED 显示器")
public class LED extends NodeModbusBase {
    @Override
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return false;
    }

    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.WRITE_REGISTER;
        regOffset = 0;  //308;
        regCount = 1;   // 寄存器的数量
    }


    @Override
    public Object genMessage(int where, int nodeId, int cmd, int value) {
        if (cmd == ConstantCommandType.GENERAL_READ) {
            return null;
        }

        ChannelNode cn = nodeManager.getChannelNodeByNodeId(nodeId).channelNode;

        ModbusMessage frame = new ModbusMessage();

        frame.from = where;
        frame.nodeId = nodeId;
        frame.type = cmd;

        frame.payload = ModbusFrame.writeRegister(cn.getAdr(), regOffset, value);
        frame.responseLen = 8;

        return frame;
    }

    /**
     * @param msg
     */
    @Override
    public void decodeMessage(Object msg) {
        // 这里可以什么都不做
        ModbusMessage modbusMessage = (ModbusMessage) msg;

        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = modbusMessage.from;
        nodeMsg.type = modbusMessage.type;
        nodeMsg.node = modbusMessage.nodeId;

        ByteBuf payload = modbusMessage.payload;

        nodeMsg.value = payload.getShort(4);

        payload.release();

        nodeManager.onMessage(nodeMsg);
    }

    /**
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

        outMsg = (ModbusMessage) genMessage(msg.from, msg.node, msg.type, (Integer) msg.value);
        channel.sendMsg(outMsg);

        return rst;
    }
}

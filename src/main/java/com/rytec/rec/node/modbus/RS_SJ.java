package com.rytec.rec.node.modbus;

import com.rytec.rec.channel.ModbusTcpServer.ModbusMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.NodeRuntimeBean;
import com.rytec.rec.node.ValueCompare;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Service
@AnnotationNodeType(2002)
@AnnotationJSExport("RS-SJ 水浸")
public class RS_SJ extends NodeModbusBase {
    @Override
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return ValueCompare.booleanNeedUpdate(cfg, oldVal, newVal);
    }

    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_HOLDING_REGISTERS;
        regOffset = 2;
        regCount = 1;   // 寄存器的数量
    }

    /**
     * 处理模拟数据，这里重载父类的方法
     *
     * @param msg
     * @param data
     */
    private void processAnalog(NodeMessage msg, ByteBuf data) {
        NodeRuntimeBean nodeRunTime = nodeManager.getChannelNodeByNodeId(msg.node);
        int channelId = nodeRunTime.channelNode.getId();
        HashMap<Integer, ChannelNode> nodes = (HashMap<Integer, ChannelNode>) channelManager.channelNodes.get(channelId);

        int analogVal = 0;
        int valueIndex = 0;
        for (ChannelNode node : nodes.values()) {

            // 485 地址是一致的，类型是一致的
            if ((nodeRunTime.channelNode.getAdr().intValue() == node.getAdr().intValue()) &&
                    (nodeRunTime.channelNode.getNtype().intValue() == node.getNtype().intValue())) {

                valueIndex = (node.getNo() - 1) * 2;
                analogVal = data.getShort(valueIndex);

                if (analogVal == 1) {
                    msg.value = false;
                } else {
                    msg.value = true;
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

    @Override
    public void decodeMessage(Object msg) {
        ModbusMessage modbusMessage = (ModbusMessage) msg;

        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = modbusMessage.from;
        nodeMsg.type = modbusMessage.type;
        nodeMsg.node = modbusMessage.nodeId;

        ByteBuf payload = modbusMessage.payload;
        ByteBuf data = Unpooled.buffer();

        payload.getBytes(3, data, 1);
        processAnalog(nodeMsg, data);
    }
}

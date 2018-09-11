package com.rytec.rec.node.modbus;

import com.rytec.rec.channel.Modbus.common.ModbusMessage;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.ValueCompare;
import com.rytec.rec.node.modbus.base.DmaBaseModbus;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@AnnotationNodeType(2002)
@AnnotationJSExport("RS-SJ 水浸")
public class RS_SJ extends DmaBaseModbus {
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
    }

    @Override
    public void decodeMessage(Object msg) {
        ModbusMessage modbusMessage = (ModbusMessage) msg;

        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = modbusMessage.from;
        nodeMsg.type = modbusMessage.type;
        nodeMsg.node = modbusMessage.nodeId;

        ByteBuf payload = modbusMessage.payload;
        int val = payload.getShort(3);

        // 解码
        if (val == 1) {
            nodeMsg.value = false;
        } else {
            nodeMsg.value = true;
        }

        // 反向
        if (nodeManager.getChannelNodeByNodeId(nodeMsg.node).nodeConfig.pA < 0) {
            nodeMsg.value = !((Boolean) nodeMsg.value);
        }

        nodeManager.onMessage(nodeMsg);

        payload.release();
    }
}

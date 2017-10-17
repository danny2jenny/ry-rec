package com.rytec.rec.node.modbus;

import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.NodeRuntimeBean;
import com.rytec.rec.node.ValueCompare;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;


/**
 * 第一个是湿度0，
 * 第二个是温度1
 */
@Service
@AnnotationNodeType(2003)
@AnnotationJSExport("RF-WSD 温湿度")
public class RS_WSD extends NodeModbusBase {
    @Override
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return ValueCompare.analogNeedUpdate(cfg, oldVal, newVal);
    }

    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_HOLDING_REGISTERS;
        regCount = 2;   // 寄存器的数量
    }

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

                valueIndex = (node.getNo()-1) * 2;
                analogVal = data.getShort(valueIndex);
                currentNodeRuntime = nodeManager.getChannelNodeByNodeId(node.getNid());
                // 这里需要进行特殊的数据，处理温度低于0的情况
                if (node.getNo()==1){
                    // 湿度数据
                    msg.value = analogVal/10 + currentNodeRuntime.nodeConfig.pB;
                } else {
                    // 温度数据
                    if (analogVal>1000){
                        // 零下
                        analogVal = analogVal - 65536;
                    }
                    msg.value = analogVal + currentNodeRuntime.nodeConfig.pB;
                }

                msg.node = node.getNid();
                nodeManager.onMessage(msg);
            }
        }
        data.release();

    }
}

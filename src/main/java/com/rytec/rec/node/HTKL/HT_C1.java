package com.rytec.rec.node.HTKL;

import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.NodeRuntimeBean;
import com.rytec.rec.node.node.NodeAnalog;
import com.rytec.rec.util.AnnotationDescription;
import com.rytec.rec.util.AnnotationNodeType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 17-2-8.
 * <p>
 * 设置485地址
 * 广播地址  功能号   地址     新地址   CRC
 * 00       06      2000    0001    421b    发送
 * 01       06      2000    0001    43ca    返回
 * <p>
 * 数据读取
 * 地址   功能号     寄存器     读取数量    CRC
 * 02     03        002a      0001      a5f1
 * <p>
 * 返回数据
 * 地址   功能号     ？？        值      CRC
 * 02     03        02        0000     fc44
 * <p>
 * ？？02应该是返回的整数的小数点位数。这里应该是0.01
 */

@Service
//@AnnotationNodeType(2201)
@AnnotationDescription("HT-C1风速")
public class HT_C1 extends NodeAnalog implements NodeInterface {

    @Autowired
    NodeManager nodeManager;

    // 命令编码
    public ChannelMessage genMessage(int where, int nodeId, int cmd, int value) {

        NodeRuntimeBean nodeRuntimeBean = nodeManager.getChannelNodeByNodeId(nodeId);

        ChannelMessage frame = new ChannelMessage();

        frame.from = where;
        frame.nodeId = nodeId;
        frame.type = cmd;
        frame.responseLen = 7;

        // Unpooled.Buffer
        ByteBuf buf = Unpooled.buffer(6);
        buf.writeByte(nodeRuntimeBean.channelNode.getAdr());
        buf.writeByte(0x03);
        buf.writeShort(nodeRuntimeBean.channelNode.getNo());
        buf.writeShort(0x01);

        frame.payload = buf;

        return frame;
    }

    // 回应解码
    public NodeMessage decodeMessage(ChannelMessage msg) {


        NodeRuntimeBean nodeRuntimeBean = nodeManager.getChannelNodeByNodeId(msg.nodeId);

        NodeMessage rst = new NodeMessage();
        rst.from = msg.from;
        rst.type = msg.type;
        rst.node = msg.nodeId;
        ByteBuf payload = (ByteBuf) msg.payload;

        int val = payload.getShort(3);
        rst.value = val * nodeRuntimeBean.nodeConfig.pA + nodeRuntimeBean.nodeConfig.pB;
        payload.release();
        return rst;
    }

}

package com.rytec.rec.node.DAM;

import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.NodeRuntimeBean;
import com.rytec.rec.node.node.NodeInput;
import com.rytec.rec.util.ConstantCommandType;
import io.netty.buffer.ByteBuf;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * Created by Danny on 2017/2/23.
 * DAM 的基础类型
 */
public abstract class DAM_BASE extends NodeInput implements NodeInterface {

    @Autowired
    NodeManager nodeManager;

    @Autowired
    ChannelManager channelManager;

    //消息解码
    public void decodeMessage(ChannelMessage msg) {

        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = msg.from;
        nodeMsg.type = msg.type;
        ByteBuf payload = (ByteBuf) msg.payload;
        int val = 0;

        switch (msg.type) {
            case ConstantCommandType.GENERAL_WRITE:
                val = ((ByteBuf) msg.payload).getByte(4);
                break;
            case ConstantCommandType.GENERAL_READ:
                val = ((ByteBuf) msg.payload).getByte(3);
                break;
        }

        payload.release();

        /*
         * 需要位解码
         * 1、得到该通道下的所有ChannelNode
         * 2、遍历后，得到状态
         */

        NodeRuntimeBean msgNode = nodeManager.getChannelNodeByNodeId(msg.nodeId);
        int channelId = msgNode.channelNode.getId();
        HashMap<Integer, ChannelNode> nodes = channelManager.channelNodes.get(channelId);
        for (ChannelNode node : nodes.values()) {

            // 485 地址是一致的，类型是一致的
            if ((msgNode.channelNode.getAdr().intValue() == node.getAdr().intValue()) &&
                    (msgNode.channelNode.getNtype().intValue() == node.getNtype().intValue())) {

                //是一个地址的设备，位操作后
                int adrMask = 0x01 << node.getNo();
                if ((adrMask & val) > 0) {
                    nodeMsg.value = true;
                } else {
                    nodeMsg.value = false;
                }
                nodeMsg.node = node.getNid();
                nodeManager.onMessage(nodeMsg);
            }
        }
    }
}

package com.rytec.rec.node.DAM;

import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.node.ChannelNodeState;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.NodeComInterface;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.util.NodeType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-12-14.
 * <p>
 * 命令格式：
 * byte     modbus地址
 * byes     0x02 命令
 * word     端口地址，从0开始
 * word     数量：1
 * word     CRC16 (不生成)
 * <p>
 * 返回格式：
 * byte     modbus地址
 * byes     0x02 命令
 * byte     字节数
 * byte     端口值
 * word     CRC16 (不生成)
 */

@Service
@NodeType(1002)
public class DAMInput implements NodeComInterface {

    @Autowired
    NodeManager nodeManager;


    // 命令编码
    public ChannelMessage genMessage(int where, int nodeId, int cmd, int value) {

        ChannelNodeState channelNodeState = nodeManager.getChannelNodeByNodeId(nodeId);

        ChannelMessage frame = new ChannelMessage();

        frame.from = where;
        frame.nodeId = nodeId;
        frame.responseLen = 6;

        ByteBuf buf = Unpooled.buffer(6);
        buf.writeByte(channelNodeState.channelNode.getAdr());
        buf.writeByte(0x02);
        buf.writeShort(channelNodeState.channelNode.getNo());
        buf.writeShort(0x01);

        frame.payload = buf.array();

        return frame;
    }

    // 回应解码
    public int decodeMessage(Object msg) {
        byte[] in = ((ChannelMessage) msg).payload;
        return in[3];
    }

    public int sendMessage(NodeMessage nodeMsg) {
        int rst = 0;

        return rst;
    }
}

package com.rytec.rec.node.DAM;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.node.ChannelNodeManagerItem;
import com.rytec.rec.node.NodeComInterface;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.CommandType;
import com.rytec.rec.util.NodeType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by danny on 16-12-13.
 * <p>
 * 北京聚英电子相关的Modbus控制器
 * <p>
 * <p>
 * 写命令格式：
 * byte     modbus地址
 * byes     0x05 命令
 * word     端口地址，从0开始
 * word     开：FF 00, 关 00 00
 * word     CRC16 (不生成)
 * <p>
 * 返回格式：
 * byte     modbus地址
 * byes     0x05 命令
 * word     端口地址，从0开始
 * word     开：FF 00, 关 00 00
 * word     CRC16 (不生成)
 * <p>
 * -----------------------------------
 * 读命令：
 * byte     modbus地址
 * byes     0x01 命令
 * word     端口地址，从0开始
 * word     数量 1
 * word     CRC16 (不生成)
 * <p>
 * 返回格式：
 * byte     modbus地址
 * byes     0x01 命令
 * byte     字节数 1
 * byte     开：FF , 关 00
 * word     CRC16 (不生成)
 */

@Service
@NodeType(1001)
public class DAMOutput implements NodeComInterface {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NodeManager nodeManager;

    @Autowired
    ChannelManager channelManager;

    /*
     */
    public ChannelMessage genMessage(int where, int nodeId, int cmd, int value) {

        ChannelNodeManagerItem channelNodeManagerItem = nodeManager.getChannelNodeByNodeId(nodeId);

        ChannelMessage frame = new ChannelMessage();
        frame.from = where;
        frame.nodeId = nodeId;

        frame.type = cmd;

        ByteBuf buf;
        switch (cmd) {
            // 写入
            case CommandType.GENERAL_WRITE:
                buf = Unpooled.buffer(6);
                frame.responseLen = 8;
                buf.writeByte(channelNodeManagerItem.channelNode.getAdr());     //地址
                buf.writeByte(0x05);                     //命令
                buf.writeShort(channelNodeManagerItem.channelNode.getNo());     //寄存器
                buf.writeShort(value);
                frame.payload = buf.array();
                break;

            //状态查询
            case CommandType.GENERAL_READ:
                buf = Unpooled.buffer(6);
                frame.responseLen = 6;
                buf.writeByte(channelNodeManagerItem.channelNode.getAdr());
                buf.writeByte(0x01);
                buf.writeShort(channelNodeManagerItem.channelNode.getNo());     //地址
                buf.writeShort(1);                       //查询数量
                frame.payload = buf.array();
                break;
        }

        return frame;
    }

    //消息解码
    public int decodeMessage(Object msg) {

        int value = 0;
        ChannelMessage respond = (ChannelMessage) msg;
        switch (respond.type) {
            case CommandType.GENERAL_WRITE:
                value = respond.payload[3];
                break;
            case CommandType.GENERAL_READ:
                value = respond.payload[4];
                break;
        }
        return value;
    }


    public int sendMessage(NodeMessage msg) {
        // todo: 完善错误处理
        int rst = 0;

        //找到对应的 Channel
        ChannelNodeManagerItem nodeManagerItem = nodeManager.getChannelNodeByNodeId(msg.node);

        ChannelInterface channel = ChannelManager.getChannelInterface(nodeManagerItem.channelNode.getCtype());

        // 根据 msg.value 的类型来进行相应的channelmsg生成

        ChannelMessage channelMessage = null;

        // Boolean 转换
        if (msg.value instanceof Boolean) {
            if ((Boolean) msg.value) {
                channelMessage = genMessage(msg.from, msg.node, msg.type, 0xFF00);
            } else {
                channelMessage = genMessage(msg.from, msg.node, msg.type, 0x0000);
            }
        }

        if (msg.value instanceof Integer) {
            channelMessage = genMessage(msg.from, msg.node, msg.type, (Integer) msg.value);
        }


        channel.sendMsg(channelMessage);

        return rst;
    }
}

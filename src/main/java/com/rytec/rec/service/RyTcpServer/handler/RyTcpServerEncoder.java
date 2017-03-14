package com.rytec.rec.service.RyTcpServer.handler;

import com.rytec.rec.channel.ChannelMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Danny on 2017/3/13.
 * 发送消息
 */
public class RyTcpServerEncoder extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        //发送数据
//        msg.resetReaderIndex();
//        out.writeInt(msg.readableBytes());
//        out.writeBytes(msg);
        out.writeInt(2);
        out.writeByte(0x41);
        out.writeByte(0x00);
        ctx.flush();
    }
}

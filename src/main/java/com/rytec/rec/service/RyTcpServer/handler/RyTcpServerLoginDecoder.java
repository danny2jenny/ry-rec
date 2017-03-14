package com.rytec.rec.service.RyTcpServer.handler;

import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by Danny on 2017/3/13.
 * <p>
 * 客户端登录
 */
public class RyTcpServerLoginDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        // 读取长度和Node的Id
        long len = in.readUnsignedInt();
        int funCode = in.readUnsignedByte();

        // 生成ChannelMessage
        ChannelMessage channelMessage = new ChannelMessage(ConstantFromWhere.FROM_LOGIN);
        channelMessage.responseLen = (int) len;
        channelMessage.payload = (int) funCode;
        out.add(channelMessage);
    }
}

package com.rytec.rec.service.RyTcpServer.handler;

import com.rytec.rec.util.CRC16;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Danny on 2017/3/13.
 */
public class RyTcpServerDecoder extends ByteToMessageDecoder {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        byte[] a = new byte[in.readableBytes()];
        in.readBytes(a);
        logger.debug("消息解码：" + CRC16.bytesToHexString(a));

    }
}

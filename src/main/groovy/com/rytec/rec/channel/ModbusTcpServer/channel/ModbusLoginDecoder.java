package com.rytec.rec.channel.ModbusTcpServer.channel;

import com.rytec.rec.channel.ModbusTcpServer.entity.ModbusFrame;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by danny on 16-12-9.
 * 这个是串口服务器登录后发送的第一个字节作为该串口的标识符号
 */
public class ModbusLoginDecoder extends ByteToMessageDecoder {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        ModbusFrame msg = new ModbusFrame();
        msg.type = 100;
        msg.payload = Unpooled.buffer();
        msg.payload.writeByte(in.readByte());
        out.add(msg);
    }
}

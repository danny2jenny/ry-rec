package com.rytec.rec.channel.ModbusTcpServer.handler;

import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.buffer.ByteBuf;
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
        ChannelMessage msg = new ChannelMessage();
        msg.from = ConstantFromWhere.FROM_LOGIN;
        msg.payload = new byte[1];
        msg.payload[0] = in.readByte();
        out.add(msg);
    }
}

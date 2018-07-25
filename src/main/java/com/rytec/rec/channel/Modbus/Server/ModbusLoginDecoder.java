package com.rytec.rec.channel.Modbus.Server;

import com.rytec.rec.channel.Modbus.common.ModbusMessage;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by danny on 16-12-9.
 * 这个是串口服务器登录后发送的第一个字节作为该串口的标识符号
 */
public class ModbusLoginDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        ModbusMessage msg = new ModbusMessage();
        msg.from = ConstantFromWhere.FROM_LOGIN;
        msg.payload = Unpooled.buffer(1);
        ((ByteBuf)msg.payload).writeByte(in.readByte());
        out.add(msg);
    }
}

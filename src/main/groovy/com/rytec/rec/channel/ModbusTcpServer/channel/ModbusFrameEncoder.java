package com.rytec.rec.channel.ModbusTcpServer.channel;

import com.rytec.rec.channel.ModbusTcpServer.entity.ModbusFrame;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by danny on 16-12-12.
 */
public class ModbusFrameEncoder extends MessageToByteEncoder<ModbusFrame> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusFrame msg, ByteBuf out) {

    }
}

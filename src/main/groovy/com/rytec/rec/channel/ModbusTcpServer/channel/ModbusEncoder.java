package com.rytec.rec.channel.ModbusTcpServer.channel;

import com.rytec.rec.channel.ModbusTcpServer.entity.ModbusFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Created by danny on 16-11-22.
 */
public class ModbusEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ModbusFrame) {
            ModbusFrame frame = (ModbusFrame) msg;

            ctx.writeAndFlush(frame.encode());
        } else {
            ctx.write(msg);
        }
    }
}
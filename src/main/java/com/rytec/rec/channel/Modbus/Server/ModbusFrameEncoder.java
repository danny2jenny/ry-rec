package com.rytec.rec.channel.Modbus.Server;

import com.rytec.rec.channel.Modbus.ModbusMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by danny on 16-12-12.
 * <p>
 * 把 ModbusMessage 转换成字节流进行发送
 */
public class ModbusFrameEncoder extends MessageToByteEncoder<ModbusMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusMessage msg, ByteBuf out) {

        //发送数据
        ByteBuf byteBuf = msg.payload;
        byteBuf.resetReaderIndex();
        out.writeBytes(msg.payload);
        ctx.flush();
    }
}

package com.rytec.rec.channel.ModbusTcpServer.handler;

import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.util.CRC16;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.LoggerFactory;

/**
 * Created by danny on 16-12-12.
 *
 * 把 ChannelMessage 转换成字节流进行发送
 */
public class ModbusFrameEncoder extends MessageToByteEncoder<ChannelMessage> {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void encode(ChannelHandlerContext ctx, ChannelMessage msg, ByteBuf out) {

        //发送数据
        ByteBuf byteBuf = (ByteBuf)msg.payload;
        byteBuf.resetReaderIndex();
        int crc = CRC16.calcCrc16(byteBuf.array());
        out.writeBytes((ByteBuf) msg.payload);
        out.writeShort(crc);
        ctx.flush();
    }
}

package com.rytec.rec.channel.ModbusTcpServer.channel;

import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;
import com.rytec.rec.util.CRC16;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.LoggerFactory;

/**
 * Created by danny on 16-12-12.
 */
public class ModbusFrameEncoder extends MessageToByteEncoder<ModbusFrame> {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusFrame msg, ByteBuf out) {

        int crc = CRC16.calcCrc16(msg.payload);
        out.writeBytes(msg.payload);
        out.writeShort(crc);
        logger.debug("写入数据：" + CRC16.bytesToHexString(msg.payload));
        ctx.flush();
    }
}

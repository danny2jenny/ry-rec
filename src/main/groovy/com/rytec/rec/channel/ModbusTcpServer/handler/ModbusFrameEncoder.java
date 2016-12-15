package com.rytec.rec.channel.ModbusTcpServer.handler;

import com.rytec.rec.channel.ModbusTcpServer.ChanneSession;
import com.rytec.rec.channel.ModbusTcpServer.ModbusCommon;
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

        //更新Channel状态，主要是当前的命令级别，以及期望返回的数据长度
        ChanneSession channeSession = ctx.channel().attr(ModbusCommon.MODBUS_STATE).get();
        channeSession.lastCmd = msg;

        //发送数据
        int crc = CRC16.calcCrc16(msg.payload);
        out.writeBytes(msg.payload);
        out.writeShort(crc);

        ctx.flush();
    }
}

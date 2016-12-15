package com.rytec.rec.channel.ModbusTcpServer.handler;

import com.rytec.rec.channel.ModbusTcpServer.ChanneSession;
import com.rytec.rec.channel.ModbusTcpServer.ModbusCommon;
import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;
import com.rytec.rec.util.FromWhere;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by danny on 16-12-12.
 * 可以考虑使用 ReplayingDecoder
 */
public class ModbusFrameDecoder extends ReplayingDecoder {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        ChanneSession channeSession = ctx.channel().attr(ModbusCommon.MODBUS_STATE).get();

        //读取希望返回的长度
        ByteBuf data = in.readBytes(channeSession.lastCmd.responseLen);
        byte[] payload = new byte[channeSession.lastCmd.responseLen];    //包含去除CRC校验
        data.readBytes(payload);

        ModbusFrame msg = new ModbusFrame(FromWhere.FROM_RPS);
        msg.payload = payload;

        out.add(msg);

    }

}

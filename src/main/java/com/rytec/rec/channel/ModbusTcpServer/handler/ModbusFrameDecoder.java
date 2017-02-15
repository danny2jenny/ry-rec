package com.rytec.rec.channel.ModbusTcpServer.handler;

import com.rytec.rec.channel.ModbusTcpServer.ChanneSession;
import com.rytec.rec.channel.ModbusTcpServer.ModbusCommon;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by danny on 16-12-12.
 * 可以考虑使用 ReplayingDecoder
 */
public class ModbusFrameDecoder extends ByteToMessageDecoder {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        ChanneSession channelSession = ctx.channel().attr(ModbusCommon.MODBUS_STATE).get();

        //读取希望返回的长度
        //
        // todo：超时处理可能已经销毁了lastCmd，然后才读取道返回，这个时候需要重新把 in 中的写指针重置


        if (channelSession.lastCmd == null) {
            in.skipBytes(in.readableBytes());
        } else {
            int len = channelSession.lastCmd.responseLen;

            if (in.readableBytes() < len) {
                return;
            }
            //ByteBuf data = in.readBytes(channelSession.getLastCmd().responseLen);
            ByteBuf data = in.readBytes(len);


            ChannelMessage msg = new ChannelMessage(ConstantFromWhere.FROM_RPS);
            msg.nodeId = channelSession.lastCmd.nodeId;
            msg.type = channelSession.lastCmd.type;
            msg.payload = data;
            // 清除当前发送的命令
            channelSession.lastCmd = null;
            out.add(msg);
        }
    }
}

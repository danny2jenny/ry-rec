package com.rytec.rec.channel.ModbusTcpServer.handler;

import com.rytec.rec.channel.ModbusTcpServer.ChanneSession;
import com.rytec.rec.channel.ModbusTcpServer.ModbusCommon;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.util.CRC16;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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

        ChannelMessage channelMessage = channelSession.getLastOutMsg();

        if (channelMessage == null){
            return;
        }


        if (in.readableBytes() < channelMessage.responseLen) {
            return;
        }

            /*
            * 这里应该是读取最后一段的内容，
            * 因为重头读取可能读取到上一次没有处理完的脏数据
            * 当成功收到回应后，清楚当前的缓冲
            * -----------------------
            * |  遗留数据 |  有效数据 |
            * -----------------------
            *
            * readableBytes() - len ： 需要跳过的字节
            */

        // todo: data 和 payload 可以放在 session 中增加效率

        byte[] data = new byte[channelMessage.responseLen];
        in.getBytes(in.readableBytes() - channelMessage.responseLen, data, 0, channelMessage.responseLen);
        int error = CRC16.check(data);

        if (error == 0) {

            in.skipBytes(in.readableBytes());
            // 成功组帧
            ByteBuf payload = Unpooled.buffer(channelMessage.responseLen);
            payload.setBytes(0, data);

            ChannelMessage msg = new ChannelMessage(ConstantFromWhere.FROM_RPS);
            msg.payload = payload;

            msg.nodeId = channelSession.getLastOutMsg().nodeId;
            msg.type = channelSession.getLastOutMsg().type;
            // 清除当前发送的命令
            channelSession.clearLastOutMsg();
            out.add(msg);
        }
    }
}

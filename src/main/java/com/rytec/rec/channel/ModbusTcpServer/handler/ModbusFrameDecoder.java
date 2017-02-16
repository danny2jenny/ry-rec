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

        // todo：超时处理可能已经销毁了lastCmd，然后才读取道返回，这个时候需要重新把 in 中的写指针重置
        // todo：这里应该加上CRC校验


        if (channelSession.getLastOutMsg() == null) {
            // 没有发送，但是有接收的消息，跳过接收的内容
            in.skipBytes(in.readableBytes());
        } else {
            int len = channelSession.getLastOutMsg().responseLen;

            if (in.readableBytes() < len) {
                return;
            }

            //收到完整的消息
            ChannelMessage msg = new ChannelMessage(ConstantFromWhere.FROM_RPS);
            msg.payload = in.readBytes(len);
            msg.nodeId = channelSession.getLastOutMsg().nodeId;
            msg.type = channelSession.getLastOutMsg().type;
            // 清除当前发送的命令
            channelSession.clearLastOutMsg();
            out.add(msg);
        }
    }
}

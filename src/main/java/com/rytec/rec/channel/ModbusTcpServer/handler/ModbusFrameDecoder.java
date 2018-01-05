package com.rytec.rec.channel.ModbusTcpServer.handler;

import com.rytec.rec.channel.ModbusTcpServer.ModbusChannelSession;
import com.rytec.rec.channel.ModbusTcpServer.ModbusCommon;
import com.rytec.rec.channel.ModbusTcpServer.ModbusMessage;
import com.rytec.rec.util.CRC16;
import com.rytec.rec.util.ConstantFromWhere;
import com.rytec.rec.util.Tools;
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

        // 得到Channel 对应的 Session
        ModbusChannelSession modbusChannelSession = ctx.channel().attr(ModbusCommon.MODBUS_STATE).get();

        // 当前发送的命令
        ModbusMessage lastOutMsg = modbusChannelSession.getLastOutMsg();

        // 当前读取缓冲的状态
        int inBufferLen = in.readableBytes();       // 接收数据长度
        int readIndex = in.readerIndex();           // 接收缓冲读指针

        byte[] receivedData = new byte[inBufferLen];

        if (lastOutMsg == null) {
            in.readBytes(receivedData);
            logger.debug("超时遗留数据：" + CRC16.bytesToHexString(receivedData));
            return;
        }

        // 没有足够的长度，返回
        if (inBufferLen < lastOutMsg.responseLen) {
            return;
        }

        /**
         * 1、读取所有的长度
         * 2、匹配命令字和地址
         * 3、丢弃
         */

        // 不改变读指针，得到收到的数据到 receivedData
        in.getBytes(readIndex, receivedData, 0, inBufferLen);

        int headIndex = Tools.findSubArray(receivedData, lastOutMsg.payload.copy(0, 2).array(), 0);

        while (headIndex >= 0) {

            if ((inBufferLen - headIndex) < lastOutMsg.responseLen) {
                // 剩余的数据长度不够
                return;
            }

            // ----------------- 以下可以对返回数据进行解码了 ----------------------
            byte[] aData = new byte[lastOutMsg.responseLen];    // 期望的返回数据
            System.arraycopy(receivedData, headIndex, aData, 0, lastOutMsg.responseLen);

            int error = CRC16.check(aData);

            // 没有错误
            if (error == 0) {

                // 成功组帧
                ByteBuf payload = Unpooled.buffer(lastOutMsg.responseLen);
                payload.writeBytes(aData);

                ModbusMessage msg = new ModbusMessage(ConstantFromWhere.FROM_RPS);
                msg.payload = payload;

                msg.nodeId = lastOutMsg.nodeId;
                msg.type = lastOutMsg.type;

                modbusChannelSession.goodHelth(lastOutMsg, true);

                // 清除当前发送的命令
                modbusChannelSession.clearLastOutMsg();

                out.add(msg);
                in.skipBytes(in.readableBytes());          // 全部跳过
                return;
            } else {
                // CRC错误，打印当前的内容和解码的内容
                modbusChannelSession.goodHelth(lastOutMsg, false);
                logger.debug("CRC错误：" + CRC16.bytesToHexString(aData));
                //跳过无用的数据, 再次寻找 headindex

                int preIndex = headIndex;
                headIndex = Tools.findSubArray(receivedData, lastOutMsg.payload.copy(0, 2).array(), headIndex + 2);

                if (headIndex<0){
                    in.skipBytes(preIndex + 2);
                }
            }
        }
    }
}

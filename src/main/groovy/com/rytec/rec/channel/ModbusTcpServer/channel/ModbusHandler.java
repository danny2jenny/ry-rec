package com.rytec.rec.channel.ModbusTcpServer.channel;

import com.rytec.rec.channel.ModbusTcpServer.ChannelState;
import com.rytec.rec.channel.ModbusTcpServer.ModbusCommon;
import com.rytec.rec.channel.ModbusTcpServer.ModbusTcpServer;
import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;
import com.rytec.rec.util.CRC16;
import com.rytec.rec.util.FromWhere;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;

import java.net.CacheRequest;
import java.net.InetSocketAddress;

/**
 * Created by danny on 16-12-12.
 */
public class ModbusHandler extends SimpleChannelInboundHandler<ModbusFrame> {

    private final ModbusTcpServer modbusTcpServer;

    public ModbusHandler(ModbusTcpServer server) {
        this.modbusTcpServer = server;
    }

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        modbusTcpServer.clients.remove(ctx.channel().attr(ModbusCommon.MODBUS_ID).get());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ModbusFrame response) throws Exception {
        switch (response.from) {
            // 登录
            case FromWhere.FROM_LOG:
                //设置Channel的ID
                InetSocketAddress ip = (InetSocketAddress) ctx.channel().remoteAddress();
                String modbusId = ip.getHostName() + ':' + response.payload[0];
                ctx.channel().attr(ModbusCommon.MODBUS_ID).set(modbusId);
                modbusTcpServer.clients.put(modbusId, ctx.channel());

                //设置Channel的状态
                ChannelState channelState = new ChannelState();
                ctx.channel().attr(ModbusCommon.MODBUS_STATE).set(channelState);

                //移除相应的登录解码器，添加帧解码器
                ctx.pipeline().remove("LoginDecoder");
                ctx.pipeline().addFirst("FrameDecoder", new ModbusFrameDecoder());

                //调试信息
                logger.debug("Modbus Client login from IP：" + ctx.channel().attr(ModbusCommon.MODBUS_ID).get());
                break;
            // 远端的回应
            case FromWhere.FROM_RPS:
                logger.debug("回应消息:" + CRC16.bytesToHexString(response.payload));
                break;
            default:
                break;
        }
    }

    // 错误处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

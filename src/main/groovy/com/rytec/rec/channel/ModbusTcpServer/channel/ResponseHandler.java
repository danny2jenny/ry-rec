package com.rytec.rec.channel.ModbusTcpServer.channel;

import com.rytec.rec.channel.ModbusTcpServer.ModbusTcpServer;
import com.rytec.rec.channel.ModbusTcpServer.entity.ModbusFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by danny on 16-12-12.
 */
public class ResponseHandler extends SimpleChannelInboundHandler<ModbusFrame> {

    private final ModbusTcpServer modbusTcpServer;

    public ResponseHandler(ModbusTcpServer server) {
        this.modbusTcpServer = server;
    }

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final AttributeKey<String> MODBUS_ID = AttributeKey.valueOf("modbus.id");


    @Override
    public void channelActive(ChannelHandlerContext ctx) {


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ModbusFrame response) throws Exception {
        switch (response.type) {
            // 登录
            case 100:
                //登录成功
                InetSocketAddress ip = (InetSocketAddress) ctx.channel().remoteAddress();
                String modbusId = ip.getHostName() + ':' + response.payload.readByte();
                ctx.channel().attr(MODBUS_ID).set(modbusId);
                modbusTcpServer.clients.put(modbusId, ctx.channel());

                //移除相应的登录解码器，添加帧解码器
                ctx.pipeline().remove("LoginDecoder");
                ctx.pipeline().addFirst("FrameDecoder", new ModbusFrameDecoder());

                //调试信息
                logger.debug("Modbus Client login from IP：" + ctx.channel().attr(MODBUS_ID).get());
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

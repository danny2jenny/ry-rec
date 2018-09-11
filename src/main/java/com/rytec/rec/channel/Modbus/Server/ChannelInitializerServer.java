package com.rytec.rec.channel.Modbus.Server;


import com.rytec.rec.channel.Modbus.ChannelModbusServer;
import com.rytec.rec.channel.Modbus.common.ModbusFrameEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by danny on 16-11-22.
 * <p>
 * Modbus TCPServer 的初始化
 */
public class ChannelInitializerServer extends ChannelInitializer<SocketChannel> {

    public final ChannelModbusServer modbusServer;

    public ChannelInitializerServer(ChannelModbusServer server) {
        this.modbusServer = server;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(5,	5,	0,	TimeUnit.SECONDS));
        pipeline.addFirst("FrameEndoder", new ModbusFrameEncoder());                    //发送编码
        pipeline.addLast("LoginDecoder", new ModbusLoginDecoder());                     //登录解码
        pipeline.addLast("ModbusServerHandler", new ModbusServerHandler(modbusServer)); //接收
    }
}


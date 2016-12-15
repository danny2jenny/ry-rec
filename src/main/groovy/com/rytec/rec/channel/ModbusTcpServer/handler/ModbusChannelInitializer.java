package com.rytec.rec.channel.ModbusTcpServer.handler;


import com.rytec.rec.channel.ModbusTcpServer.ModbusTcpServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by danny on 16-11-22.
 */
public class ModbusChannelInitializer extends ChannelInitializer<SocketChannel> {

    public final ModbusTcpServer modbusServer;

    public ModbusChannelInitializer(ModbusTcpServer server) {
        this.modbusServer = server;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addFirst("FrameEndoder", new ModbusFrameEncoder());                 //发送编码
        pipeline.addLast("LoginDecoder", new ModbusLoginDecoder());                  //登录解码
        pipeline.addLast("ModbusHandler", new ModbusHandler(modbusServer));          //接收
    }
}


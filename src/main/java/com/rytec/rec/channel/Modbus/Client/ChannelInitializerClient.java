package com.rytec.rec.channel.Modbus.Client;

import com.rytec.rec.channel.Modbus.ChannelModbusBase;
import com.rytec.rec.channel.Modbus.Server.ModbusFrameDecoder;
import com.rytec.rec.channel.Modbus.Server.ModbusFrameEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 编码解码顺序
 */
public class ChannelInitializerClient extends ChannelInitializer<SocketChannel> {

    private ChannelModbusBase channelModbusBase;
    private ModbusClient modbusClient;
    private int id;

    public ChannelInitializerClient(ChannelModbusBase modbusChannel, ModbusClient client, int key) {
        this.channelModbusBase = modbusChannel;
        this.modbusClient = client;
        this.id = key;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(20, 20, 0, TimeUnit.SECONDS));
        pipeline.addFirst("FrameEncoder", new ModbusFrameEncoder());                 //发送编码
        pipeline.addFirst("FrameDecoder", new ModbusFrameDecoder());                 //接收解码
        pipeline.addLast("ModbusHandler", new ModbusClientHandler(channelModbusBase, modbusClient, id));    //接收

    }
}

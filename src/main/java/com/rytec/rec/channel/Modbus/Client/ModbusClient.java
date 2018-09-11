package com.rytec.rec.channel.Modbus.Client;

import com.rytec.rec.channel.Modbus.ChannelModbusBase;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端对象
 * http://www.importnew.com/25046.html
 */
public class ModbusClient {

    String host = "";
    int port = 0;

    ChannelModbusBase channelModbusBase;

    int id = 0;

    volatile boolean shutdown = false;

    private EventLoopGroup loop = new NioEventLoopGroup();

    public ChannelFuture channelFuture = null;

    public ModbusClient(ChannelModbusBase modbusChannel, int key, String host, int port) {
        this.channelModbusBase = modbusChannel;
        this.id = key;
        this.host = host;
        this.port = port;
        createBootstrap(new Bootstrap(), loop);
    }

    public Bootstrap createBootstrap(Bootstrap bootstrap, EventLoopGroup eventLoop) {
        if (bootstrap != null) {
            bootstrap.group(eventLoop);    //这句应该可以不要
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializerClient(channelModbusBase, this, id));
            bootstrap.remoteAddress(host, port);

            this.channelFuture = bootstrap.connect();
            channelFuture.addListener(new ConnectionListener(this));
        }
        return bootstrap;
    }

    /**
     * 关闭连接
     */
    public void close() {
        shutdown = true;
        try {
            channelFuture.channel().close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.rytec.rec.channel.Modbus.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

import java.util.concurrent.TimeUnit;

/**
 * 监视连接状态，如果连接不成功，重新连接；
 */
public class ConnectionListener implements ChannelFutureListener {
    private ModbusClient client;

    public ConnectionListener(ModbusClient client) {
        this.client = client;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            System.out.println("Reconnect");
            final EventLoop loop = channelFuture.channel().eventLoop();

            if (!client.shutdown) {
                loop.schedule(new Runnable() {
                    @Override
                    public void run() {
                        client.createBootstrap(new Bootstrap(), loop);
                    }
                }, 1L, TimeUnit.SECONDS);
            }
        }
    }
}


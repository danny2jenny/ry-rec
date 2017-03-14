package com.rytec.rec.service.RyTcpServer.handler;

import com.rytec.rec.service.RyTcpServer.RyTcpServer;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by Danny on 2017/3/13.
 * 基于长度的解码
 */
public class RyTcpServerInitializer extends ChannelInitializer<Channel> {

    // 服务器的引用
    private RyTcpServer ryTcpServer;

    public RyTcpServerInitializer(RyTcpServer server) {
        this.ryTcpServer = server;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 基于长度的解码
        pipeline.addLast("FrameDepack", new LengthFieldBasedFrameDecoder(65 * 1024, 0, 4));
        pipeline.addLast("FrameEndoder", new RyTcpServerEncoder());         // 消息编码
        pipeline.addLast("LoginDecoder", new RyTcpServerLoginDecoder());    // 消息登录
        pipeline.addLast(new RyTcpServerHandler(ryTcpServer));              // 解码后的处理
    }

}

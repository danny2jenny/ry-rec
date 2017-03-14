package com.rytec.rec.service.RyTcpServer;

import io.netty.channel.Channel;

/**
 * Created by Danny on 2017/3/13.
 */
public class RyClientSession {

    private RyTcpServer ryTcpServer;
    private Channel channel;
    private int funId;

    public int getFunId() {
        return funId;
    }

    public RyClientSession(RyTcpServer server, int funId, Channel channel) {
        this.ryTcpServer = server;
        this.channel = channel;
        this.funId = funId;
    }



}

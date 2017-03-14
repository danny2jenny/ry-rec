package com.rytec.rec.service.RyTcpServer;

import io.netty.util.AttributeKey;

/**
 * Created by Danny on 2017/3/13.
 */
public class RyTcpServerCommon {
    public static final AttributeKey<RyClientSession> TCP_STATE = AttributeKey.valueOf("tcp.session");
}

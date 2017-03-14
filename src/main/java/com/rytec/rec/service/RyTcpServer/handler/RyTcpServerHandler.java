package com.rytec.rec.service.RyTcpServer.handler;

import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.service.RyTcpServer.RyClientSession;
import com.rytec.rec.service.RyTcpServer.RyTcpServer;
import com.rytec.rec.service.RyTcpServer.RyTcpServerCommon;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.LoggerFactory;

/**
 * Created by Danny on 2017/3/13.
 */
public class RyTcpServerHandler extends SimpleChannelInboundHandler<ChannelMessage> {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private RyTcpServer ryTcpServer;

    RyTcpServerHandler(RyTcpServer server) {
        this.ryTcpServer = server;
    }

    /**
     * 连接建立
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.debug("客户端连接：");
    }

    /**
     * 连接断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 错误处理
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        RyClientSession session = ctx.channel().attr(RyTcpServerCommon.TCP_STATE).get();
        if (session != null) {
            ryTcpServer.clients.remove(session.getFunId());
        }
        ctx.close();
    }

    /**
     * 超时的处理
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 当一个channel太久没有写入数据，表示这个Channel是没有配置的，应该断开。
        if (evt instanceof IdleStateEvent) {
            ctx.close();
            logger.debug("无效的连接！！！！！！！！！！！！");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChannelMessage response) throws Exception {
        switch (response.from) {
            // 登录
            case ConstantFromWhere.FROM_LOGIN:
                // 加入到连接列表中
                ryTcpServer.clients.put((Integer) response.payload, ctx.channel());
                RyClientSession ryClientSession = new RyClientSession(ryTcpServer, (Integer) response.payload, ctx.channel());
                ctx.channel().attr(RyTcpServerCommon.TCP_STATE).set(ryClientSession);

                //移除相应的登录解码器，添加帧解码器
                ctx.pipeline().remove("LoginDecoder");
                ctx.pipeline().addAfter("FrameEndoder", "FrameDecoder", new RyTcpServerDecoder());

                // 通知服务器登录消息
                ryTcpServer.onLogin((Integer) response.payload);
                break;
            // 普通消息
            case ConstantFromWhere.FROM_RPS:
                break;
        }
    }
}

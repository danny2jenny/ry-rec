package com.rytec.rec.channel.Modbus.Client;

import com.rytec.rec.channel.Modbus.ChannelModbusBase;
import com.rytec.rec.channel.Modbus.ModbusTcpSession;
import com.rytec.rec.channel.Modbus.ModbusCommon;
import com.rytec.rec.channel.Modbus.ModbusMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.TimeUnit;

/**
 * 所有的接收消息在这里进行处理
 */
public class ModbusClientHandler extends SimpleChannelInboundHandler<ModbusMessage> {

    private ChannelModbusBase channelModbusBase;
    private ModbusClient client;
    private int id;

    /**
     * 构造函数
     *
     * @param serv
     * @param client
     */
    public ModbusClientHandler(ChannelModbusBase serv, ModbusClient client, int key) {
        this.channelModbusBase = serv;
        this.id = key;
        this.client = client;
    }

    /**
     * 连接建立
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 建立Session
        ModbusTcpSession modbusTcpSession = new ModbusTcpSession(channelModbusBase, id, ctx.channel());
        ctx.channel().attr(ModbusCommon.MODBUS_STATE).set(modbusTcpSession);
    }

    /**
     * 意外断线后的重新连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final EventLoop eventLoop = ctx.channel().eventLoop();

        // 判断是否重连接
        if (!client.shutdown) {
            eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                    client.createBootstrap(new Bootstrap(), eventLoop);
                }
            }, 1L, TimeUnit.SECONDS);
        }

        super.channelInactive(ctx);
    }

    /**
     * 收到数据
     *
     * @param ctx
     * @param response
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ModbusMessage response) throws Exception {
        // 得到当前通道对应的Session
        ModbusTcpSession modbusTcpSession = ctx.channel().attr(ModbusCommon.MODBUS_STATE).get();

        channelModbusBase.receiveMsg(modbusTcpSession.id, response);
        modbusTcpSession.clearLastOutMsg();
    }
}
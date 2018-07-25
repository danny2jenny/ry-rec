package com.rytec.rec.channel.Modbus.Client;

import com.rytec.rec.channel.Modbus.ChannelModbusBase;
import com.rytec.rec.channel.Modbus.common.ModbusTcpSession;
import com.rytec.rec.channel.Modbus.common.ModbusCommon;
import com.rytec.rec.channel.Modbus.common.ModbusMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.TimeUnit;

/**
 * 所有的接收消息在这里进行处理
 */
public class ModbusClientHandler extends SimpleChannelInboundHandler<ModbusMessage> {

    private ChannelModbusBase channelModbus;
    private ModbusClient client;
    private int id;

    /**
     * 构造函数
     *
     * @param channelModbus
     * @param client
     */
    public ModbusClientHandler(ChannelModbusBase channelModbus, ModbusClient client, int key) {
        this.channelModbus = channelModbus;
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
        ModbusTcpSession modbusTcpSession = new ModbusTcpSession(channelModbus, id, ctx.channel());
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
            System.out.print("通讯错误，重新连接");
            eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                    client.createBootstrap(new Bootstrap(), eventLoop);
                }
            }, 5, TimeUnit.SECONDS);
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

        channelModbus.receiveMsg(modbusTcpSession.id, response);
        modbusTcpSession.clearLastOutMsg();
    }
}

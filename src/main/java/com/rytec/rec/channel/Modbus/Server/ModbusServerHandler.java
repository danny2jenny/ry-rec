package com.rytec.rec.channel.Modbus.Server;

import com.rytec.rec.channel.Modbus.ChannelModbusBase;
import com.rytec.rec.channel.Modbus.common.ModbusTcpSession;
import com.rytec.rec.channel.Modbus.common.ModbusCommon;
import com.rytec.rec.channel.Modbus.common.ModbusMessage;
import com.rytec.rec.channel.Modbus.common.ModbusFrameDecoder;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.InetSocketAddress;

/**
 * Created by danny on 16-12-12.
 * <p>
 * 所有收到的Modbus帧在这里进行处理
 */
public class ModbusServerHandler extends SimpleChannelInboundHandler<ModbusMessage> {

    // 由于Natty的机制，这里不能用Autowired的方式
    private ChannelModbusBase channelModbusBase;

    ModbusServerHandler(ChannelModbusBase serv) {
        this.channelModbusBase = serv;
    }

    /**
     * 读取消息
     *
     * @param ctx
     * @param response
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ModbusMessage response) throws Exception {

        // 得到当前通道对应的Session
        ModbusTcpSession modbusTcpSession = ctx.channel().attr(ModbusCommon.MODBUS_STATE).get();

        switch (response.from) {
            // 登录
            case ConstantFromWhere.FROM_LOGIN:


                //设置Channel的ID
                InetSocketAddress ip = (InetSocketAddress) ctx.channel().remoteAddress();
                String modbusId = ip.getHostName() + ':' + ((ByteBuf) response.payload).readByte();

                //建立外部通过ip:port可以访问Channel的接口
                channelModbusBase.addClient(modbusId, ctx.channel());
                channelModbusBase.channelOnline(modbusId, true);

                //设置Channel的Session
                modbusTcpSession = new ModbusTcpSession(channelModbusBase, modbusId, ctx.channel());
                ctx.channel().attr(ModbusCommon.MODBUS_STATE).set(modbusTcpSession);

                //移除相应的登录解码器，添加帧解码器
                ctx.pipeline().remove("LoginDecoder");
                ctx.pipeline().addFirst("FrameDecoder", new ModbusFrameDecoder());
                break;
            // 远端的回应：这里一定是正确的回应
            case ConstantFromWhere.FROM_RPS:
                channelModbusBase.receiveMsg(modbusTcpSession.id, response);
                modbusTcpSession.clearLastOutMsg();
                // modbusTcpSession.timerProcess();
                break;
            default:
                break;
        }
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
            channelModbusBase.debug("无效的连接！！！！！！！！！！！！");
        }
    }

    /**
     * 连接建立
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {


    }

    /**
     * 连接断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ModbusTcpSession session = ctx.channel().attr(ModbusCommon.MODBUS_STATE).get();

        if (session != null) {
            channelModbusBase.delClient(session.id);
            channelModbusBase.channelOnline(session.id, false);
        }

    }

}

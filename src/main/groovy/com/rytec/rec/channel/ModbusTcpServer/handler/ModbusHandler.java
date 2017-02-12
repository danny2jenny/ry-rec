package com.rytec.rec.channel.ModbusTcpServer.handler;

import com.rytec.rec.channel.ModbusTcpServer.ChanneSession;
import com.rytec.rec.channel.ModbusTcpServer.ModbusCommon;
import com.rytec.rec.channel.ModbusTcpServer.ModbusTcpServer;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * Created by danny on 16-12-12.
 * <p>
 * 所有收到的Modbus帧在这里进行处理
 */
public class ModbusHandler extends SimpleChannelInboundHandler<ChannelMessage> {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    // 由于Natty的机制，这里不能用Autowired的方式
    private ModbusTcpServer modbusTcpServer;

    ModbusHandler(ModbusTcpServer serv) {
        this.modbusTcpServer = serv;
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
        modbusTcpServer.clients.remove(ctx.channel().attr(ModbusCommon.MODBUS_STATE).get().id);
    }

    /**
     * 读取消息
     *
     * @param ctx
     * @param response
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChannelMessage response) throws Exception {

        // 得到当前通道对应的Session
        ChanneSession channeSession = ctx.channel().attr(ModbusCommon.MODBUS_STATE).get();

        switch (response.from) {
            // 登录
            case ConstantFromWhere.FROM_LOGIN:

                //设置Channel的ID
                InetSocketAddress ip = (InetSocketAddress) ctx.channel().remoteAddress();
                String modbusId = ip.getHostName() + ':' + ((ByteBuf)response.payload).readByte();

                logger.debug("客户端连接：" + modbusId);

                //建立外部通过ip:port可以访问Channel的接口
                modbusTcpServer.clients.put(modbusId, ctx.channel());

                //设置Channel的Session
                channeSession = new ChanneSession(modbusId, ctx.channel());
                ctx.channel().attr(ModbusCommon.MODBUS_STATE).set(channeSession);

                //移除相应的登录解码器，添加帧解码器
                ctx.pipeline().remove("LoginDecoder");
                ctx.pipeline().addFirst("FrameDecoder", new ModbusFrameDecoder());

                //设置查询命令集合
                HashMap<Integer, ChannelNode> cha = modbusTcpServer.channelNodes.get(modbusId);

                for (ChannelNode cn : cha.values()) {
                    NodeInterface node = modbusTcpServer.nodeManager.getNodeComInterface(cn.getNtype());
                    ChannelMessage msg = node.genMessage(ConstantFromWhere.FROM_TIMER, cn.getNid(), ConstantCommandType.GENERAL_READ, 0);
                    channeSession.timerQueryCmd.add(msg);
                }
                break;
            // 远端的回应
            case ConstantFromWhere.FROM_RPS:
                modbusTcpServer.receiveMsg(channeSession.id, channeSession.lastCmd, response);

                // 清除当前发送的命令
                channeSession.lastCmd = null;
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
}

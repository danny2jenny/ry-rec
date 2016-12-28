package com.rytec.rec.channel.ModbusTcpServer.handler;

import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.channel.ModbusTcpServer.ChanneSession;
import com.rytec.rec.channel.ModbusTcpServer.ModbusCommon;
import com.rytec.rec.channel.ModbusTcpServer.ModbusTcpServer;
import com.rytec.rec.channel.ModbusTcpServer.ModbusMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeProtocolInterface;
import com.rytec.rec.util.CommandType;
import com.rytec.rec.util.FromWhere;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by danny on 16-12-12.
 * <p>
 * 所有收到的Modbus帧在这里进行处理
 */
public class ModbusHandler extends SimpleChannelInboundHandler<ModbusMessage> {

    @Autowired
    ChannelManager channelManager;

    private final ModbusTcpServer modbusTcpServer;

    public ModbusHandler(ModbusTcpServer server) {
        this.modbusTcpServer = server;
    }

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        modbusTcpServer.clients.remove(ctx.channel().attr(ModbusCommon.MODBUS_STATE).get().id);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ModbusMessage response) throws Exception {

        ChanneSession channeSession = ctx.channel().attr(ModbusCommon.MODBUS_STATE).get();

        switch (response.from) {
            // 登录
            case FromWhere.FROM_LOG:

                //设置Channel的ID
                InetSocketAddress ip = (InetSocketAddress) ctx.channel().remoteAddress();
                String modbusId = ip.getHostName() + ':' + response.payload[0];

                logger.debug("客户端连接：" + modbusId);

                //建立外部通过ip:port可以访问Channel的借口
                modbusTcpServer.clients.put(modbusId, ctx.channel());

                //设置Channel的状态
                channeSession = new ChanneSession(modbusId, ctx.channel());

                //移除相应的登录解码器，添加帧解码器
                ctx.pipeline().remove("LoginDecoder");
                ctx.pipeline().addFirst("FrameDecoder", new ModbusFrameDecoder());

                //设置查询命令集合
                ConcurrentHashMap<Integer, ChannelNode> cha = modbusTcpServer.channelNodes.get(modbusId);

                for (ChannelNode cn : cha.values()) {
                    NodeProtocolInterface node = NodeManager.getNode(cn.getNtype());
                    ModbusMessage msg = (ModbusMessage) node.genMessage(FromWhere.FROM_TIME, cn.getNid(), CommandType.MODBUS_CMD_READ, 0);
                    channeSession.queryCmd.add(msg);
                }
                ctx.channel().attr(ModbusCommon.MODBUS_STATE).set(channeSession);

                break;
            // 远端的回应
            case FromWhere.FROM_RPS:
                modbusTcpServer.receiveMsg(channeSession.id, channeSession.lastCmd, response);
                channeSession.lastCmd = null;
                break;
            default:
                break;
        }
    }

    // 错误处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

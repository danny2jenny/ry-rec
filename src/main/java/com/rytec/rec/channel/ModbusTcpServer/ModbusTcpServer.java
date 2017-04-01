/**
 * Created by danny on 16-11-22.
 * <p>
 * ModbusTcp Server
 */

package com.rytec.rec.channel.ModbusTcpServer;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.channel.ModbusTcpServer.handler.ModbusChannelInitializer;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.util.AnnotationChannelType;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.ConstantErrorCode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Order(400)
@AnnotationChannelType(1001)
@AnnotationJSExport("Modbus服务器")
public class ModbusTcpServer implements ChannelInterface, ManageableInterface {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    //端口配置
    @Value("${tcp.server.modbus.port}")
    private int port;

    //nety 服务
    private ServerBootstrap bootstrap;
    private Channel parentChannel;

    // 对应的客户端连接列表
    public final ConcurrentHashMap<String, Channel> clients = new ConcurrentHashMap<>();

    // 建立服务器
    @PostConstruct
    private void startServer() {

        // 读取数据库的配置
        initConfig();

        // 建立 TCP Server
        try {
            // 监听端口
            final EventLoopGroup bossGroup = new NioEventLoopGroup();
            // 工作端口
            final EventLoopGroup workerGroup = new NioEventLoopGroup();

            // 一个新的netty服务器引导对象
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 数据流处理的pip，完成所有的数据处理逻辑
                    .childHandler(new ModbusChannelInitializer(this))
                    // 连接队列的长度
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            // ChannelFuture 对象，同步调用
            parentChannel = bootstrap.bind(port).sync().channel();

            //添加一个关闭事件的监听
            parentChannel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
                // Sever 关闭完成时调用，匿名对象
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }
            });
        } catch (Exception ex) {
            // todo : 错误处理
            //throw new ConnectionException(ex.getLocalizedMessage());
        }
    }


    //关闭服务，这里是同步还是异步？
    @PreDestroy
    private void stopServer() {
        if (parentChannel != null) {
            parentChannel.close().awaitUninterruptibly();
        }
        clients.clear();
        parentChannel = null;
        channelNodes.clear();
    }

    //----------------------------------对通道的初始化-----------------------------

    @Autowired
    private DbConfig dbConfig;

    public HashMap<String, HashMap> channelNodes = new HashMap();

    /*
    * 初始化对应的HashMap
    * 两级 HashMap
    * 第一级：ip:port->Map
    * 第二级：nodeId->ChannelNode
     */
    private void initConfig() {

        List<ChannelNode> chaNodeList = dbConfig.getChannelNodeList();

        //第一级的Map ip:id-> map
        for (ChannelNode cn : chaNodeList) {
            if (cn.getCtype() != 1001) {        //只管理该类型的Channel
                continue;
            }

            //每一个Channel的标识是IP：PORT
            String chaId = cn.getIp() + ':' + cn.getPort();

            //是否已经存在该Channel
            HashMap<Integer, ChannelNode> cha = channelNodes.get(chaId);

            //不存在，建立该Channel
            if (cha == null) {
                cha = new HashMap();
                this.channelNodes.put(chaId, cha);
            }

            //node的标识是 nid
            cha.put(cn.getNid(), cn);
        }
    }

    /**
     * Session 是一个生成的对象，无法使用Spring管理，
     * 因此在这里一个定时任务去执行所有Session的任务
     * 通讯率 9600
     * 每秒1000个字节（双向累计）
     * 每个通讯过程20个字节（最大）
     * 最多可以是50个通讯过程
     * 每个过程约20个毫秒
     * 因此50~100个毫秒是安全的
     * <p>
     * 每个Channel（Socket 连接都需要进行轮训）通过一个HashMap来进行
     * <String, Integer>   ip:port -> 当前的轮训位置
     */

    @Scheduled(fixedDelay = 100)
    private void doOnTime() {
        // 遍历已经登录的远端，并执行队列
        for (Channel cha : clients.values()) {
            ModbusChannelSession modbusChannelSession = cha.attr(ModbusCommon.MODBUS_STATE).get();
            if (modbusChannelSession != null) {
                modbusChannelSession.timerProcess();
            }
        }
    }

    @Autowired
    public NodeManager nodeManager;

    /**
     * Channel 解码后调用该过程
     *
     * @param chaId    ip:port 的形式
     * @param response 回应消息
     */
    public void receiveMsg(String chaId, ChannelMessage response) {

        ChannelNode cn = (ChannelNode) channelNodes.get(chaId).get(response.nodeId);
        NodeInterface nodeBean = nodeManager.getNodeComInterface(cn.getNtype());

        // 解码值
        nodeBean.decodeMessage(response);

    }

    /**
     * 实现接口方法
     *
     * @param msg
     */
    public int sendMsg(Object msg) {
        int rst = 0;
        ChannelNode channelNode = nodeManager.getChannelNodeByNodeId(((ChannelMessage) msg).nodeId).channelNode;
        String channelId = channelNode.getIp() + ':' + channelNode.getPort();
        Channel channel = clients.get(channelId);
        if (channel == null) {
            return ConstantErrorCode.CHA_NOT_CONNECT;
        } else {
            ModbusChannelSession modbusChannelSession = channel.attr(ModbusCommon.MODBUS_STATE).get();
            modbusChannelSession.sendMsg((ChannelMessage)msg);
        }
        return rst;
    }

    public void stop() {
        //关闭现有的连接
        stopServer();
    }

    public void start() {
        startServer();
    }
}

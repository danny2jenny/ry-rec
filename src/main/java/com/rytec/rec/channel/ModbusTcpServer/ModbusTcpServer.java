/**
 * Created by danny on 16-11-22.
 * <p>
 * ModbusTcp Server
 */

package com.rytec.rec.channel.ModbusTcpServer;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.channel.ModbusTcpServer.handler.ModbusChannelInitializer;
import com.rytec.rec.channel.ModbusTcpServer.exception.ConnectionException;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.AnnotationChannelType;
import com.rytec.rec.util.ConstantErrorCode;
import com.rytec.rec.util.AnnotationDescription;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AnnotationChannelType(1001)
@AnnotationDescription("Modbus TCP Server")
public class ModbusTcpServer implements ChannelInterface {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    //端口配置
    @Value("${tcp.server.modbus.port}")
    private int port;

    private ServerBootstrap bootstrap;

    private Channel parentChannel;

    // 对应的客户端连接列表
    public final ConcurrentHashMap<String, Channel> clients = new ConcurrentHashMap<>();

    // 建立服务器
    @PostConstruct
    public void setup() throws ConnectionException {

        // 读取数据库的配置
        initConfig();

        // 建立 TCP Server
        try {
            final EventLoopGroup bossGroup = new NioEventLoopGroup();
            final EventLoopGroup workerGroup = new NioEventLoopGroup();

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
            Logger.getLogger(ModbusTcpServer.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
            throw new ConnectionException(ex.getLocalizedMessage());
        }
    }


    //关闭服务，这里是同步还是异步？
    @PreDestroy
    public void close() {
        if (parentChannel != null) {
            parentChannel.close().awaitUninterruptibly();
        }
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

    @Scheduled(fixedDelay = 50)
    private void doOnTime() {
        // 遍历已经登录的远端，并执行队列
        for (Channel cha : clients.values()) {
            ChanneSession channeSession = cha.attr(ModbusCommon.MODBUS_STATE).get();
            channeSession.timerProcess();
        }
    }

    @Autowired
    public NodeManager nodeManager;

    /**
     * Channel 解码后调用该过程
     *
     * @param chaId    ip:port 的形式
     * @param request  请求消息
     * @param response 回应消息
     */
    public void receiveMsg(String chaId, ChannelMessage response) {

        ChannelNode cn = (ChannelNode) channelNodes.get(chaId).get(response.nodeId);
        NodeInterface nodeBean = nodeManager.getNodeComInterface(cn.getNtype());

        // 解码值

        NodeMessage val = nodeBean.decodeMessage(response);

        nodeManager.onMessage(val);
    }

    /**
     * 实现接口方法
     *
     * @param msg
     */
    public int sendMsg(ChannelMessage msg) {
        int rst = 0;
        ChannelNode channelNode = nodeManager.getChannelNodeByNodeId(msg.nodeId).channelNode;
        String channelId = channelNode.getIp() + ':' + channelNode.getPort();
        Channel channel = clients.get(channelId);
        if (channel == null) {
            return ConstantErrorCode.CHA_NOT_CONNECT;
        } else {
            ChanneSession channeSession = channel.attr(ModbusCommon.MODBUS_STATE).get();
            channeSession.sendMsg(msg);
        }
        return rst;
    }

    /**
     * Node 的通讯错误处理
     *
     * @param cm
     */
    public void nodeError(ChannelMessage cm) {
        logger.debug("超时：" + cm.nodeId);
    }
}

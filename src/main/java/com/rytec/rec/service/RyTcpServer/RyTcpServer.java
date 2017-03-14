package com.rytec.rec.service.RyTcpServer;

import com.rytec.rec.service.RyAbstractService;
import com.rytec.rec.service.RyTcpServer.handler.RyTcpServerInitializer;
import com.rytec.rec.util.AnnotationServiceType;
import com.rytec.rec.util.ConstantErrorCode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Danny on 2017/2/22.
 * 所有自定义通信协议的的通信，前四个字节（双子）存放帧长度的方式。
 * 这个channel不作为配置用，作为一个公用的服务
 * 只管理Channel的type从2000~2999的设备
 * 通信协议：
 */

@Service
public class RyTcpServer {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    //端口配置
    @Value("${tcp.server.tcp.port}")
    private int port;

    @Autowired
    ApplicationContext context;

    // 具体Servie 的实现
    private Map<Integer, RyAbstractService> ryServiceList = new ConcurrentHashMap();

    /**
     * 对应的客户端连接列表 key 表示功能编号，目前有Video和61850
     * video: 101
     * 61850: 201
     */
    public final ConcurrentHashMap<Integer, Channel> clients = new ConcurrentHashMap<>();

    //nety 服务
    private ServerBootstrap bootstrap;
    private Channel parentChannel;

    // 初始化数据
    private void initConfig() {
        Map<String, Object> ryServiceObjects = context.getBeansWithAnnotation(AnnotationServiceType.class);
        for (Object item : ryServiceObjects.values()) {
            Class<? extends Object> objectClass = item.getClass();
            AnnotationServiceType annotation = objectClass.getAnnotation(AnnotationServiceType.class);
            ryServiceList.put(annotation.value(), (RyAbstractService) item);
        }
    }

    public RyAbstractService getRyService(int type) {
        return ryServiceList.get(type);
    }

    // 建立服务器
    @PostConstruct
    private void startServer() {

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
                    .childHandler(new RyTcpServerInitializer(this))
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
            // todo: 错误处理
        }
    }

    //关闭服务，这里是同步还是异步？
    @PreDestroy
    private void stopServer() {
        if (parentChannel != null) {
            parentChannel.close().awaitUninterruptibly();
        }
        parentChannel = null;
    }


    // 当客户端登入
    public void onLogin(int srvId) {
        RyAbstractService service = getRyService(srvId);
        if (service != null) {
            service.onLogin();
        }

    }


    /**
     * 发送消息
     *
     * @param serviceId 使用哪个服务 101/201
     * @param payload   数据
     */
    public int sendMsg(int serviceId, ByteBuf payload) {
        Channel channel = clients.get(serviceId);
        if (channel == null) {
            return ConstantErrorCode.SRV_NOT_ACTIVE;
        }
        payload.resetReaderIndex();
        channel.write(payload);
        return 0;
    }

}

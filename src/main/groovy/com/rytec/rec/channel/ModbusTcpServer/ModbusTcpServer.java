package com.rytec.rec.channel.ModbusTcpServer;

import com.rytec.rec.channel.ModbusTcpServer.channel.ModbusChannelInitializer;
import com.rytec.rec.channel.ModbusTcpServer.exception.ConnectionException;
import com.rytec.rec.util.ChannelType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by danny on 16-11-22.
 */

@Service
@ChannelType(1001)
public class ModbusTcpServer {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    //端口配置
    @Value("${tcp.server.modbus.port}")
    private int port;

    private ServerBootstrap bootstrap;

    private Channel parentChannel;

    // 对应的客户端连接列表
    public final ConcurrentHashMap<String, Channel> clients = new ConcurrentHashMap<String, Channel>();

    // 建立服务器
    @PostConstruct
    public void setup() throws ConnectionException {
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

            logger.debug(parentChannel.toString());

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


}

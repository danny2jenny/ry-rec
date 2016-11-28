package com.rytec.rec.channel.ModbusTcpServer;

import com.rytec.rec.channel.ModbusTcpServer.channel.ModbusChannelInitializer;
import com.rytec.rec.channel.ModbusTcpServer.channel.ModbusRequestHandler;
import com.rytec.rec.channel.ModbusTcpServer.exception.ConnectionException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by danny on 16-11-22.
 */
public class ModbusTcpServer {

    public static enum CONNECTION_STATES {

        listening, down, clientsConnected
    }

    public static final String PROP_CONNECTIONSTATE = "connectionState";

    private final int port;

    private ServerBootstrap bootstrap;

    private CONNECTION_STATES connectionState = CONNECTION_STATES.down;

    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private Channel parentChannel;

    // 对应的客户端连接列表
    private final ChannelGroup clientChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //构造方法
    public ModbusTcpServer(int port) {
        this.port = port;
    }

    // 建立服务器
    public void setup(ModbusRequestHandler handler) throws ConnectionException {

        handler.setServer(this);

        try {
            final EventLoopGroup bossGroup = new NioEventLoopGroup();
            final EventLoopGroup workerGroup = new NioEventLoopGroup();

            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 数据流处理的pip，完成所有的数据处理逻辑
                    .childHandler(new ModbusChannelInitializer(handler))
                    // 连接队列的长度
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            // ChannelFuture 对象，同步调用
            parentChannel = bootstrap.bind(port).sync().channel();

            setConnectionState(CONNECTION_STATES.listening);

            //添加一个关闭事件的监听
            parentChannel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {

                // Sever 关闭完成时调用，匿名对象
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();

                    setConnectionState(CONNECTION_STATES.down);
                }
            });
        } catch (Exception ex) {
            setConnectionState(CONNECTION_STATES.down);
            Logger.getLogger(ModbusTcpServer.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());

            throw new ConnectionException(ex.getLocalizedMessage());
        }
    }

    public CONNECTION_STATES getConnectionState() {
        return connectionState;
    }

    // 状态改变的时候触发
    public void setConnectionState(CONNECTION_STATES connectionState) {
        CONNECTION_STATES oldConnectionState = this.connectionState;
        this.connectionState = connectionState;
        propertyChangeSupport.firePropertyChange(PROP_CONNECTIONSTATE, oldConnectionState, connectionState);
    }


    //添加状态改变的时候触发的方法？
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    //删除状态改变的时候触发的方法？
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    //关闭服务，这里是同步还是异步？
    public void close() {
        if (parentChannel != null) {
            parentChannel.close().awaitUninterruptibly();
        }
        clientChannels.close().awaitUninterruptibly();
    }

    // 没看懂，好像是对应的客户端的连接？
    public ChannelGroup getClientChannels() {
        return clientChannels;
    }


    // 添加一个客户端连接
    public void addClient(Channel channel) {
        clientChannels.add(channel);
        setConnectionState(CONNECTION_STATES.clientsConnected);
    }

    // 删除一个客户端连接
    public void removeClient(Channel channel) {
        clientChannels.remove(channel);
        setConnectionState(CONNECTION_STATES.clientsConnected);
    }
}

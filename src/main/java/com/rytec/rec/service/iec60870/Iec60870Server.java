package com.rytec.rec.service.iec60870;

import com.rytec.rec.app.RecBase;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.Server;
import org.openmuc.j60870.ServerEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 60870 的服务器构建，并管理对应的连接
 */
@Service
@Order(400)
public class Iec60870Server extends RecBase implements ServerEventListener {

    private int connectionIdCounter = 1;

    //端口配置
    @Value("${iec60870.port}")
    private int port;
    
    @Value("${iec60870.xml}")
    public String xmlFileName;

    @Value("${iec60870.addr}")
    public int Iec60870Addr;            // 60870 地址

    @Autowired
    public FileManager fileManager;

    Server.Builder builder;
    Server server;

    public long timerOffset;            // 时间差
    /**
     * 每一个连接生成一个连接对象
     * @param connection
     */
    @Override
    public void connectionIndication(Connection connection) {
        int myConnectionId = connectionIdCounter++;
        logger.debug("A client has connected using TCP/IP. Will listen for a StartDT request. Connection ID: "
                + myConnectionId);

        try {
            Iec60870Listener iec60870Listener = new Iec60870Listener(connection, myConnectionId);
            // 为非托管类传递一些参数
            iec60870Listener.setServer(this);


            connection.waitForStartDT(iec60870Listener, 15000);
        } catch (IOException e) {
            logger.debug("Connection (" + myConnectionId + ") interrupted while waiting for StartDT: "
                    + e.getMessage() + ". Will quit.");
            return;
        } catch (TimeoutException e) {
        }

        logger.debug(
                "Started data transfer on connection (" + myConnectionId + ") Will listen for incoming commands.");

    }

    @Override
    public void serverStoppedListeningIndication(IOException e) {
        logger.debug(
                "Server has stopped listening for new connections : \"" + e.getMessage() + "\". Will quit.");
    }

    @Override
    public void connectionAttemptFailed(IOException e) {
        logger.debug("Connection attempt failed: " + e.getMessage());
    }

    @PostConstruct
    public void start() {
        builder = new Server.Builder();
        builder.setPort(port);

        server = builder.build();

        try {
            server.start(this);
        } catch (IOException e) {
            logger.debug("Unable to start listening: \"" + e.getMessage() + "\". Will quit.");
            return;
        }
    }

    @PreDestroy
    public void stop() {
        server.stop();
    }
}

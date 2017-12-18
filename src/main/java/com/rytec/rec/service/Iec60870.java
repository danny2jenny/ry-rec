package com.rytec.rec.service;

import com.rytec.rec.app.RecBase;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.Server;
import org.openmuc.j60870.ServerEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class Iec60870 extends RecBase implements ServerEventListener {

    private int connectionIdCounter = 1;

    //端口配置
    @Value("${iec60870.port}")
    private int port;

    @Autowired
    SystemTime systemTime;

    Server.Builder builder;
    Server server;

    @Override
    public void connectionIndication(Connection connection) {
        int myConnectionId = connectionIdCounter++;
        logger.debug("A client has connected using TCP/IP. Will listen for a StartDT request. Connection ID: "
                + myConnectionId);

        try {
            Iec60870Listener iec60870Listener = new Iec60870Listener(connection, myConnectionId);
            iec60870Listener.systemTime = systemTime;
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

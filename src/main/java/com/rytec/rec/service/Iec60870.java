package com.rytec.rec.service;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.Server;
import org.openmuc.j60870.ServerEventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class Iec60870 implements ServerEventListener {
    private int connectionIdCounter = 1;

    Server server;

    @Override
    public void connectionIndication(Connection connection) {
        int myConnectionId = connectionIdCounter++;
        System.out.println("A client has connected using TCP/IP. Will listen for a StartDT request. Connection ID: "
                + myConnectionId);

        try {
            connection.waitForStartDT(new Iec60870Listener(connection, myConnectionId), 5000);
        } catch (IOException e) {
            System.out.println("Connection (" + myConnectionId + ") interrupted while waiting for StartDT: "
                    + e.getMessage() + ". Will quit.");
            return;
        } catch (TimeoutException e) {
        }

        System.out.println(
                "Started data transfer on connection (" + myConnectionId + ") Will listen for incoming commands.");

    }

    @Override
    public void serverStoppedListeningIndication(IOException e) {
        System.out.println(
                "Server has stopped listening for new connections : \"" + e.getMessage() + "\". Will quit.");
    }

    @Override
    public void connectionAttemptFailed(IOException e) {
        System.out.println("Connection attempt failed: " + e.getMessage());
    }

    @PostConstruct
    public void start() {
        server = new Server.Builder().build();

        try {
            server.start(this);
        } catch (IOException e) {
            System.out.println("Unable to start listening: \"" + e.getMessage() + "\". Will quit.");
            return;
        }
    }

    @PreDestroy
    public void stop(){
        server.stop();
    }
}

package com.rytec.rec.service;

import org.openmuc.j60870.*;

import java.io.EOFException;
import java.io.IOException;

public class Iec60870Listener implements ConnectionEventListener {
    private final Connection connection;
    private final int connectionId;

    public Iec60870Listener(Connection connection, int connectionId) {
        this.connection = connection;
        this.connectionId = connectionId;
    }

    @Override
    public void newASdu(ASdu aSdu) {
        try {

            switch (aSdu.getTypeIdentification()) {
                // interrogation command
                case C_IC_NA_1:
                    connection.sendConfirmation(aSdu);
                    System.out.println("Got interrogation command. Will send scaled measured values.\n");

                    connection.send(new ASdu(TypeId.M_ME_NB_1, true, CauseOfTransmission.SPONTANEOUS, false, false,
                            0, aSdu.getCommonAddress(),
                            new InformationObject[]{new InformationObject(1, new InformationElement[][]{
                                    {new IeScaledValue(-32768), new IeQuality(true, true, true, true, true)},
                                    {new IeScaledValue(10), new IeQuality(true, true, true, true, true)},
                                    {new IeScaledValue(-5), new IeQuality(true, true, true, true, true)}})}));

                    break;
                default:
                    System.out.println("Got unknown request: " + aSdu + ". Will not confirm it.\n");
            }

        } catch (EOFException e) {
            System.out.println("Will quit listening for commands on connection (" + connectionId
                    + ") because socket was closed.");
        } catch (IOException e) {
            System.out.println("Will quit listening for commands on connection (" + connectionId
                    + ") because of error: \"" + e.getMessage() + "\".");
        }

    }

    @Override
    public void connectionClosed(IOException e) {
        System.out.println("Connection (" + connectionId + ") was closed. " + e.getMessage());
    }
}

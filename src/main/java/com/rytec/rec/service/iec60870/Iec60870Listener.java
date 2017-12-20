package com.rytec.rec.service.iec60870;

import com.rytec.rec.app.RecBase;
import com.rytec.rec.service.SystemTime;
import org.openmuc.j60870.*;

import java.io.EOFException;
import java.io.IOException;

/**
 * 60870 通讯处理
 */
public class Iec60870Listener extends RecBase implements ConnectionEventListener {

    private final Connection connection;
    private final int connectionId;

    public SystemTime systemTime;

    ASdu asduBack;

    public Iec60870Listener(Connection connection, int connectionId) {
        this.connection = connection;
        this.connectionId = connectionId;
    }

    /**
     * 所有的命令应答都在这里
     *
     * @param aSdu 收到的命令结构
     */
    @Override
    public void newASdu(ASdu aSdu) {
        try {

            switch (aSdu.getTypeIdentification()) {
                // interrogation command
                case C_IC_NA_1:
                    connection.sendConfirmation(aSdu);
                    logger.debug("Got interrogation command. Will send scaled measured values.\n");

                    connection
                            .send(new ASdu(TypeId.M_ME_NB_1, true, CauseOfTransmission.SPONTANEOUS, false, false, 0,
                                    aSdu.getCommonAddress(),
                                    new InformationObject[]{new InformationObject(1, new InformationElement[][]{
                                            {new IeScaledValue(-32768), new IeQuality(true, true, true, true, true)},
                                            {new IeScaledValue(10), new IeQuality(true, true, true, true, true)},
                                            {new IeScaledValue(-5), new IeQuality(true, true, true, true, true)}})}));

                    break;
                case C_CS_NA_1: // 对时命令
                    /**
                     * 只保持远端时间和本地时间的差值。 发送时间的时候，加上这个差值。
                     */
                    IeTime56 time56 = (IeTime56) aSdu.getInformationObjects()[0].getInformationElements()[0][0];
                    long currentTimestamp = System.currentTimeMillis();
                    long receivedTimestamp = time56.getTimestamp();
                    systemTime.timerOffset = receivedTimestamp - currentTimestamp;

                    asduBack = new ASdu(TypeId.C_CS_NA_1, true, CauseOfTransmission.ACTIVATION_CON, false, false, 0,
                            aSdu.getCommonAddress(), new InformationObject[]{new InformationObject(1,
                            new InformationElement[][]{{new IeTime56(receivedTimestamp)}})});
                    connection.send(asduBack);
                    break;

                /**
                 * 文件传输：目录召唤
                 */
                case F_SC_NA_1:

                    byte[] filename = {0x31, 0x32, 0x33, 0x2E, 0x6A, 0x70, 0x67, 0x00};

                    asduBack = new ASdu(TypeId.F_DR_TA_1, 1, CauseOfTransmission.REQUEST, false, false, 0,
                            aSdu.getCommonAddress(),
                            new InformationObject[]{
                                    new InformationObject(0,
                                            new InformationElement[][]{{new IeNameOfFile(0)},
                                                    {new IeLengthOfFileOrSection(3234)},
                                                    {new IeStatusOfFile(0, false, false, false)},
                                                    {new IeTime56(System.currentTimeMillis())}, {new IeChecksum(0x31)},
                                                    {new IeFileName(filename)}
                                            }

                                    ),
                                    new InformationObject(1,
                                            new InformationElement[][]{
                                                    {new IeNameOfFile(1)},
                                                    {new IeLengthOfFileOrSection(3234)},
                                                    {new IeStatusOfFile(0, false, false, false)},
                                                    {new IeTime56(System.currentTimeMillis())},
                                                    {new IeFileName(filename)}
                                            }

                                    )});
                    connection.send(asduBack);
                    logger.debug("文件目录");
                    break;
                default:
                    logger.debug("Got unknown request: " + aSdu + ". Will not confirm it.\n");
            }

        } catch (EOFException e) {
            logger.debug(
                    "Will quit listening for commands on connection (" + connectionId + ") because socket was closed.");
        } catch (IOException e) {
            logger.debug("Will quit listening for commands on connection (" + connectionId + ") because of error: \""
                    + e.getMessage() + "\".");
        }

    }

    @Override
    public void connectionClosed(IOException e) {
        logger.debug("Connection (" + connectionId + ") was closed. " + e.getMessage());
    }
}

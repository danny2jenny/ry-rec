package com.rytec.rec.service.iec60870;

import com.rytec.rec.app.RecBase;

import org.openmuc.j60870.*;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 60870 通讯处理 每个客户端对应一个该对象
 */
public class Iec60870Listener extends RecBase implements ConnectionEventListener {

    private static int SEGMENT_MAX_SIZE = 236;                // 每一段的最大容量
    private static int SEGMENT_MAX_COUNT = 255;            // 每一节最大段的数量

    private final Connection connection;
    private final int connectionId;

    ASdu asduBack;

    private Iec60870Server iec60870Server;

    // ----------------------- 通讯状态 ----------------------------
    FileInputStream fileSend;                           // 当前上传文件
    int sectionAmt = 0;                                 // 总节数
    int remainSize = 0;                                 // 剩余大小
    int sectionLen = 0;                                 // 当前Section大小
    int section = 0;                                    // 当前节
    int segment = 0;                                    // 当前段
    int fileSize = 0;                                   // 文件大小
    int fileName = 0;                                   // 文件名称

    // -----------------------------------------------------------

    public void setServer(Iec60870Server server) {
        iec60870Server = server;
    }

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

                    asduBack = new ASdu(TypeId.C_CS_NA_1, true, CauseOfTransmission.ACTIVATION_CON, false, false, 0,
                            aSdu.getCommonAddress(), new InformationObject[]{new InformationObject(1,
                            new InformationElement[][]{{new IeTime56(receivedTimestamp)}})});
                    connection.send(asduBack);
                    break;

                /**
                 * 文件传输：目录召唤
                 */
                case F_SC_NA_1:
                    switch (aSdu.getCauseOfTransmission()) {
                        case REQUEST: // 目录请求
                            asduBack = iec60870Server.asduFileList.getAsdu(aSdu);
                            connection.send(asduBack);
                            break;
                        case FILE_TRANSFER: // 文件传输
                            // connection.fileReady();cd
                            InformationElement[] ies = aSdu.getInformationObjects()[0].getInformationElements()[0];
                            int fileIndex, fileSection, fileRequest;
                            fileIndex = ((IeNameOfFile) ies[0]).getValue();
                            fileSection = ((IeNameOfSection) ies[1]).getValue();
                            fileRequest = ((IeSelectAndCallQualifier) ies[2]).getRequest();

                            logger.debug("文件命令:" + fileRequest);
                            switch (fileRequest) {
                                case C_FileRequest.FILE_SELECT_FILE:        // 选择文件
                                    prepairFile(fileIndex);
                                    break;
                                case C_FileRequest.FILE_REQUST_FILE:        // 请求文件
                                    fileSectionReady();
                                    break;
                                case C_FileRequest.FILE_REQUST_SECTION:     // 请求节
                                    fileSendSegment();
                                    break;
                            }

                            break;
                        default:
                            break;
                    }
                    break;
                case F_AF_NA_1:     // 节回应
                    logger.debug("节回应-------------------");
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

    /**
     * 准备文件
     *
     * @param index 文件名，索引
     *              <p>
     *              文档对应：
     *              节的大小>段的大小
     *              按照文档：
     *              Section = 节
     *              Segment = 段
     */
    private void prepairFile(int index) {

        // 首先关闭以前的文件
        if (fileSend != null) {
            try {
                fileSend.close();
            } catch (IOException e) {
                return;
            }

            fileSend = null;
        }

        // TODO: 目前只有配置文件
        if (index != 0) {
            try {
                connection.fileReady(0, 0,
                        new IeNameOfFile(fileName),                                // 文件名称
                        new IeLengthOfFileOrSection(0),                    // 文件大小
                        new IeFileReadyQualifier(0, true));    // 是否准备好
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }

        // 如果文件不存在，返回
        File file = new File(iec60870Server.xmlFileName);
        if (!file.exists()) {
            try {
                connection.fileReady(0, 0,
                        new IeNameOfFile(fileName),                                // 文件名称
                        new IeLengthOfFileOrSection(0),                    // 文件大小
                        new IeFileReadyQualifier(0, true));    // 是否准备好
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }

        // 打开文件流
        try {
            fileSend = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            try {
                connection.fileReady(0, 0,
                        new IeNameOfFile(fileName),                                // 文件名称
                        new IeLengthOfFileOrSection(0),                    // 文件大小
                        new IeFileReadyQualifier(0, true));    // 是否准备好
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }

        // 计算文件的分段和分节，以及更新大小
        fileSize = (int) file.length();
        sectionAmt = (int) Math.ceil(((double) fileSize) / SEGMENT_MAX_COUNT / SEGMENT_MAX_SIZE);
        section = 0;                                    // 当前节
        segment = 0;                                    // 当前段

        try {
            connection.fileReady(0, 0,
                    new IeNameOfFile(fileName),                                // 文件名称
                    new IeLengthOfFileOrSection(fileSize),                    // 文件大小
                    new IeFileReadyQualifier(0, false));    // 是否准备好
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 节准备就绪
     */
    private void fileSectionReady() {
        remainSize = fileSize - section * SEGMENT_MAX_COUNT * SEGMENT_MAX_SIZE;
        if (remainSize >= SEGMENT_MAX_COUNT * SEGMENT_MAX_SIZE) {
            sectionLen = SEGMENT_MAX_COUNT * SEGMENT_MAX_SIZE;
        } else {
            sectionLen = remainSize;
        }

        try {
            connection.sectionReady(0, 0, new IeNameOfFile(fileName),            // 文件名称
                    new IeNameOfSection(section),                                // 节名称
                    new IeLengthOfFileOrSection(sectionLen),                            // 节长度
                    new IeSectionReadyQualifier(0, false));                        // 是否准备好
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 发送节
     */
    private void fileSendSegment() {
        int segmentCount = (int) Math.ceil(((double) sectionLen) / SEGMENT_MAX_SIZE);
        byte[] buffer = new byte[SEGMENT_MAX_SIZE];
        int segmentLen;
        // 循环发送Segment
        for (int i = 0; i < segmentCount; i++) {

            if (i < segmentCount - 1) {
                // 不是最后一段
                segmentLen = SEGMENT_MAX_SIZE;
            } else {
                // 是最后一段
                segmentLen = sectionLen - i * SEGMENT_MAX_SIZE;
            }


            try {
                fileSend.read(buffer, 0, segmentLen);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                connection.sendSegment(
                        0,
                        0,
                        new IeNameOfFile(fileName),
                        new IeNameOfSection(section),
                        new IeFileSegment(buffer, 0, segmentLen));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 发送段确认
        ASdu aSdu = new ASdu(
                TypeId.F_LS_NA_1,
                false,
                CauseOfTransmission.FILE_TRANSFER,
                false, false,
                0,
                0,
                new InformationObject[]{
                        new InformationObject(0,
                                new InformationElement[][]{
                                        {
                                                new IeNameOfFile(fileName),
                                                new IeNameOfSection(section),
                                                new IeChecksum(3)
                                        }
                                })
                }
        );

        try {
            connection.send(aSdu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

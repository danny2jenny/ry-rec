package com.rytec.rec.service.iec60870;

import com.rytec.rec.app.RecBase;

import org.openmuc.j60870.*;

import java.io.*;

/**
 * 60870 通讯处理 每个客户端对应一个该对象
 */
public class Iec60870Listener extends RecBase implements ConnectionEventListener {

    private static int SEGMENT_MAX_SIZE = 236;                  // 每一段的最大容量
    private static int SEGMENT_MAX_COUNT = 255;                 // 每一节最大段的数量

    private final Connection connection;
    private final int connectionId;

    ASdu asduBack;

    private Iec60870Server iec60870Server;

    // ----------------------- 通讯状态 ----------------------------
    FileInputStream fileSendStream;                     // 当前上传文件
    int sectionAmt = 0;                                 // 总节数
    int remainSize = 0;                                 // 剩余大小
    int sectionLen = 0;                                 // 当前Section大小
    int section = 0;                                    // 当前节
    int segment = 0;                                    // 当前段
    int fileSize = 0;                                   // 文件大小
    int fileNameIndex = 0;                              // 文件名称
    String fileNameStr;                                 // 文件名称，字符串

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
                case C_IC_NA_1:             // 站召
                    break;
                case C_CS_NA_1:             // 对时命令
                    /**
                     * 只保持远端时间和本地时间的差值。 发送时间的时候，加上这个差值。
                     */
                    IeTime56 time56 = (IeTime56) aSdu.getInformationObjects()[0].getInformationElements()[0][0];
                    long currentTimestamp = System.currentTimeMillis();
                    long receivedTimestamp = time56.getTimestamp();

                    iec60870Server.timerOffset = receivedTimestamp - currentTimestamp;

                    asduBack = new ASdu(
                            TypeId.C_CS_NA_1,
                            1,
                            CauseOfTransmission.ACTIVATION_CON,
                            false,
                            false,
                            0,
                            iec60870Server.Iec60870Addr,
                            new InformationObject[]{
                                    new InformationObject(
                                            0,
                                            new InformationElement[][]{
                                                    {
                                                            new IeTime56(receivedTimestamp)
                                                    }
                                            }
                                    )
                            }
                    );
                    connection.send(asduBack);
                    break;

                /**
                 * 文件传输：目录召唤
                 */
                case F_SC_NA_1:
                    switch (aSdu.getCauseOfTransmission()) {
                        case REQUEST:           // 目录请求
                            sendFileList();
                            break;
                        case FILE_TRANSFER:     // 文件传输
                            InformationElement[] ies = aSdu.getInformationObjects()[0].getInformationElements()[0];
                            int fileIndex, fileSection, fileRequest;
                            fileIndex = ((IeNameOfFile) ies[0]).getValue();                 // 文件名
                            fileSection = ((IeNameOfSection) ies[1]).getValue();            // 节
                            fileRequest = ((IeSelectAndCallQualifier) ies[2]).getRequest(); // 请求类型

                            logger.debug("文件命令:" + fileRequest);
                            switch (fileRequest) {
                                case C_FileRequest.FILE_SELECT_FILE:        // 选择文件
                                    sendFileReady(fileIndex);
                                    break;
                                case C_FileRequest.FILE_REQUST_FILE:        // 请求文件
                                    sendSectionReady();
                                    break;
                                case C_FileRequest.FILE_REQUST_SECTION:     // 请求节
                                    sendSegment();
                                    break;
                            }

                            break;
                        default:
                            break;
                    }
                    break;
                case F_AF_NA_1:     // 节回应
                    InformationElement[] ies = aSdu.getInformationObjects()[0].getInformationElements()[0];
                    int fileRequest = ((IeAckFileOrSectionQualifier) ies[2]).getRequest();


                    switch (fileRequest) {
                        case 1:             // 文件接收成功
                            sendFileEndInfo();
                            break;
                        case 2:             // 文件接收失败
                            break;
                        case 3:             // 节成功
                            sendLastSectionInfo();
                            break;
                        case 4:             // 节失败
                            break;
                    }

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
     * 发送文件目录
     */
    private void sendFileList() {
        FileInfo fileInfo = iec60870Server.fileManager.getFileInfo(0);
        if (fileInfo == null) {
            return;
        }

        asduBack = new ASdu(
                TypeId.F_DR_TA_1,
                1,
                CauseOfTransmission.REQUEST,
                false,
                false,
                0,
                iec60870Server.Iec60870Addr,
                new InformationObject[]{
                        new InformationObject(0,
                                new InformationElement[][]{
                                        {
                                                new IeNameOfFile(0),      // 文件名称Index
                                                new IeLengthOfFileOrSection(fileInfo.size),    // 文件长度
                                                new IeStatusOfFile(
                                                        0,
                                                        false,
                                                        false,
                                                        false), // 文件状态
                                                new IeTime56(fileInfo.modifyTime), // 修改时间
                                                new IeFileNameStr(fileInfo.name.getBytes()),  // 文件名称
                                                new IeChecksum(0)
                                        }
                                }

                        )});

        try {
            connection.send(asduBack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭已经打开的文件
     */
    private void closeFile() {
        if (fileSendStream == null) {
            return;
        }
        try {
            fileSendStream.close();
            fileSendStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送准备文件
     *
     * @param index 文件名，索引
     *              文档对应：
     *              节的大小>段的大小
     *              按照文档：
     *              Section = 节
     *              Segment = 段
     */
    private void sendFileReady(int index) {

        closeFile();
        FileInfo fileInfo = iec60870Server.fileManager.getFileInfo(index);
        boolean fileReady = false;

        if (fileInfo != null) {
            fileSize = fileInfo.size;
            fileNameIndex = index;
            fileNameStr = fileInfo.name;
            sectionAmt = (int) Math.ceil(((double) fileSize) / SEGMENT_MAX_COUNT / SEGMENT_MAX_SIZE);
            section = 0;                                    // 当前节
            segment = 0;                                    // 当前段
            fileReady = true;
        }

        // 计算文件的分段和分节，以及更新大小

        try {
            fileSendStream = new FileInputStream(new File(fileInfo.fullName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection.fileReady(
                    iec60870Server.Iec60870Addr,
                    0,
                    new IeNameOfFile(fileNameIndex),                          // 文件名称
                    new IeLengthOfFileOrSection(fileSize),                    // 文件大小
                    new IeFileReadyQualifier(0, !fileReady));           // 是否准备好
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 节准备就绪
     */
    private void sendSectionReady() {
        remainSize = fileSize - section * SEGMENT_MAX_COUNT * SEGMENT_MAX_SIZE;

        if (remainSize >= SEGMENT_MAX_COUNT * SEGMENT_MAX_SIZE) {
            sectionLen = SEGMENT_MAX_COUNT * SEGMENT_MAX_SIZE;
        } else {
            sectionLen = remainSize;
        }

        try {
            connection.sectionReady(
                    iec60870Server.Iec60870Addr,
                    0,
                    new IeNameOfFile(fileNameIndex),  // 文件名称
                    new IeNameOfSection(section),                                                       // 节名称
                    new IeLengthOfFileOrSection(sectionLen),                                            // 节长度
                    new IeSectionReadyQualifier(0, false)
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 发送节
     */
    private void sendSegment() {
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
                fileSendStream.read(buffer, 0, segmentLen);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                connection.sendSegment(
                        iec60870Server.Iec60870Addr,
                        0,
                        new IeNameOfFile(fileNameIndex),
                        new IeNameOfSection(section),
                        new IeFileSegment(buffer, 0, segmentLen)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 发送段确认
        ASdu aSdu = new ASdu(
                TypeId.F_LS_NA_1,
                1,
                CauseOfTransmission.FILE_TRANSFER,
                false, false,
                0,
                iec60870Server.Iec60870Addr,
                new InformationObject[]{
                        new InformationObject(
                                0,
                                new InformationElement[][]{
                                        {
                                                new IeNameOfFile(fileNameIndex),
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

    /**
     * 通知最后一节传输完成
     */
    private void sendLastSectionInfo() {
        ASdu aSdu = new ASdu(
                TypeId.F_LS_NA_1,
                1,
                CauseOfTransmission.FILE_TRANSFER,
                false,
                false,
                0,
                iec60870Server.Iec60870Addr,
                new InformationObject[]{
                        new InformationObject(
                                0,
                                new InformationElement[][]{
                                        {
                                                new IeNameOfFile(fileNameIndex),
                                                new IeNameOfSection(section),
                                                new IeChecksum(1)
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

    /**
     * 文件发送成功的确认
     */
    private void sendFileEndInfo() {
        ASdu aSdu = new ASdu(
                TypeId.F_DR_TA_1,
                1,
                CauseOfTransmission.REQUEST,
                false,
                false,
                0,
                iec60870Server.Iec60870Addr,
                new InformationObject[]{
                        new InformationObject(
                                0,
                                new InformationElement[][]{
                                        {
                                                new IeNameOfFile(fileNameIndex),
                                                new IeLengthOfFileOrSection(fileSize),
                                                new IeChecksum(0x80),
                                                new IeTime56(System.currentTimeMillis()),
                                                new IeFileNameStr(fileNameStr.getBytes()),  // 文件名称
                                                new IeChecksum(0)
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



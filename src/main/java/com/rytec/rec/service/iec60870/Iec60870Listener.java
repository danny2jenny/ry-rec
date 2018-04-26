package com.rytec.rec.service.iec60870;

import com.rytec.rec.app.RecBase;

import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.device.state.StateAnalog;
import com.rytec.rec.device.state.StateCurrent;
import com.rytec.rec.device.state.StateFiber;
import com.rytec.rec.device.state.StateOutput;
import org.openmuc.j60870.*;

import java.io.*;

/**
 * 60870 通讯处理 每个客户端对应一个该对象
 */
public class Iec60870Listener extends RecBase implements ConnectionEventListener {

    private static int STATE_ON = 21;                           // 开关状态值 开
    private static int STATE_OFF = 20;                          // 开关状态值 关

    private static int SEGMENT_MAX_SIZE = 236;                  // 每一段的最大容量
    private static int SEGMENT_MAX_COUNT = 255;                 // 每一节最大段的数量

    private final Connection connection;
    private final int connectionId;

    private Iec60870Server iec60870Server;

    // ----------------------- 通讯状态 ----------------------------
    FileInputStream fileSendStream;                     // 当前上传文件
    int sectionAmt = 0;                                 // 总节数
    int remainSize = 0;                                 // 剩余大小
    int crtSectionLen = 0;                              // 当前Section大小
    int crtSectionIndex = 0;                            // 当前节
    int crtSegmentIndex = 0;                            // 当前段
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

        switch (aSdu.getTypeIdentification()) {
            case C_IC_NA_1:             // 站召
                sendAllData();
                break;
            case C_CS_NA_1:             // 对时命令
                IeTime56 time56 = (IeTime56) aSdu.getInformationObjects()[0].getInformationElements()[0][0];
                sendTimeSync(time56.getTimestamp());
                break;
            case F_SC_NA_1: //文件传输
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

                        switch (fileRequest) {
                            case C_FileRequest.FILE_SELECT_FILE:        // 选择文件
                                logger.debug("M-选择文件:" + fileIndex);
                                sendFileReady(fileIndex);
                                break;
                            case C_FileRequest.FILE_REQUST_FILE:        // 请求文件，并说明Section
                                logger.debug("M-请求文件:" + fileIndex);
                                sendSectionReady();                     // 第一个节发送完成后，需要继续发送剩余的节
                                break;
                            case C_FileRequest.FILE_REQUST_SECTION:     // 请求节,可能请求不同的节 Section
                                logger.debug("M-请求节:" + fileIndex);
                                sendSection(fileSection);
                                break;
                        }

                        break;
                    default:
                        break;
                }
                break;
            case F_AF_NA_1:     // 节回应
                InformationElement[] ies = aSdu.getInformationObjects()[0].getInformationElements()[0];
                int fileSection = ((IeNameOfSection) ies[1]).getValue();
                int fileRequest = ((IeAckFileOrSectionQualifier) ies[2]).getRequest();

                switch (fileRequest) {
                    case 1:             // 文件接收成功
                        sendFileEnd();
                        closeFile();
                        break;
                    case 2:             // 文件接收失败
                        logger.debug("M-文件接收失败");
                        closeFile();
                        break;
                    case 3:             // 节成功，判断是否是最后的节，如果不是，发送下一节
                        logger.debug("M-节完成：" + crtSectionIndex);
                        crtSectionIndex++;
                        if (crtSectionIndex == sectionAmt) {
                            // 最后的 section 已经发送了
                            sendLastSectionFilish();
                        } else {
                            // 没有发送完成
                            sendSectionReady();
                        }
                        break;
                    case 4:             // 节失败，需要重新发送节
                        logger.debug("M-节失败，重新发送");
                        sendSectionReady();
                        break;
                }
                break;
            case C_SC_NA_1:             // 遥控
                InformationObject io = aSdu.getInformationObjects()[0];
                int addr = io.getInformationObjectAddress();
                boolean val = ((IeSingleCommand) (io.getInformationElements()[0][0])).isCommandStateOn();
                devControl(addr, val);
                break;
            default:
                logger.debug("Got unknown request: " + aSdu + ". Will not confirm it.\n");
        }
    }

    @Override
    public void connectionClosed(IOException e) {
        closeFile();
        logger.debug("Connection (" + connectionId + ") was closed!!!+. " + e.getMessage());
        iec60870Server.clientClosed(this);
    }

    /**
     * 发送对时响应
     */
    private void sendTimeSync(long timestamp) {
        long currentTimestamp = System.currentTimeMillis();
        long receivedTimestamp = timestamp;

        iec60870Server.timerOffset = receivedTimestamp - currentTimestamp;

        ASdu asduBack = new ASdu(
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
        try {
            connection.send(asduBack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送文件目录
     */
    private void sendFileList() {
        iec60870Server.fileManager.start();

        FileInfo fileInfo = iec60870Server.fileManager.getFileInfo(0);
        if (fileInfo == null) {
            return;
        }

        ASdu asduBack = new ASdu(
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
            crtSectionIndex = 0;                                    // 当前节
            crtSegmentIndex = 0;                                    // 当前段
            fileReady = true;
        }

        // 打开文件，以便进行后续的操作
        try {
            fileSendStream = new FileInputStream(new File(fileInfo.fullName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
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

        logger.debug("S-发送文件就绪，共：" + sectionAmt + "节");
    }

    /**
     * 节准备就绪
     */
    private void sendSectionReady() {
        remainSize = fileSize - crtSectionIndex * SEGMENT_MAX_COUNT * SEGMENT_MAX_SIZE;

        // 更新当前 Section 的大小
        if (remainSize >= SEGMENT_MAX_COUNT * SEGMENT_MAX_SIZE) {
            crtSectionLen = SEGMENT_MAX_COUNT * SEGMENT_MAX_SIZE;
        } else {
            crtSectionLen = remainSize;
        }

        try {
            connection.sectionReady(
                    iec60870Server.Iec60870Addr,
                    0,
                    new IeNameOfFile(fileNameIndex),  // 文件名称
                    new IeNameOfSection(crtSectionIndex),                                                  // 节名称
                    new IeLengthOfFileOrSection(crtSectionLen),                                            // 节长度
                    new IeSectionReadyQualifier(0, false)
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        logger.debug("S-文件传输，节：" + crtSectionIndex + "准备就绪");
    }

    /**
     * 发送节 section
     */
    private void sendSection(int section) {
        int segmentCount = (int) Math.ceil(((double) crtSectionLen) / SEGMENT_MAX_SIZE);
        byte[] buffer = new byte[SEGMENT_MAX_SIZE];
        int segmentLen;

        logger.debug("S-节发送：" + crtSectionIndex + "...");
        // 循环发送Segment
        for (int i = 0; i < segmentCount; i++) {

            if (i < segmentCount - 1) {
                // 不是最后一段
                segmentLen = SEGMENT_MAX_SIZE;
            } else {
                // 是最后一段
                segmentLen = crtSectionLen - i * SEGMENT_MAX_SIZE;
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

            logger.debug("S-段发送：" + i);
        }

        // 发最后一段完成的消息
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

        logger.debug("S-最后一段发送完成：" + crtSectionIndex + "节");
    }

    /**
     * 通知最后一节传输完成
     */
    private void sendLastSectionFilish() {
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
                                                new IeNameOfSection(crtSectionIndex - 1),
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

        logger.debug("S-最后节完成：" + (crtSectionIndex - 1));
    }

    /**
     * 文件发送成功的确认
     */
    private void sendFileEnd() {
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
        closeFile();

        logger.debug("S-文件传输完成：" + crtSectionIndex);
    }

    /**
     * 生成状态ASDU
     *
     * @param ste
     * @param active
     * @return
     */
    private ASdu genState(int addr, boolean ste, boolean active) {

        CauseOfTransmission cause;

        if (active) {
            cause = CauseOfTransmission.SPONTANEOUS;
        } else {
            cause = CauseOfTransmission.INTERROGATED_BY_STATION;
        }

        ASdu asduBack = new ASdu(
                TypeId.M_SP_NA_1,        //M_SP_TB_1,
                1,
                cause,
                false,
                false,
                0,
                iec60870Server.Iec60870Addr,
                new InformationObject[]{
                        new InformationObject(
                                addr,
                                new InformationElement[][]{
                                        {
                                                new IeSingleCommand(ste, 0, false)
                                                //new IeTime56(System.currentTimeMillis())
                                        }
                                }
                        )
                }
        );


        return asduBack;
    }

    /**
     * 生成遥测ASDU
     *
     * @param val
     * @param active
     * @return
     */
    private ASdu genMeasure(int addr, float val, boolean active) {
        CauseOfTransmission cause;

        if (active) {
            cause = CauseOfTransmission.SPONTANEOUS;
        } else {
            cause = CauseOfTransmission.INTERROGATED_BY_STATION;
        }

        ASdu asduBack = new ASdu(
                TypeId.M_ME_NC_1,           //M_ME_TF_1,
                1,
                cause,
                false,
                false,
                0,
                iec60870Server.Iec60870Addr,
                new InformationObject[]{
                        new InformationObject(
                                addr,
                                new InformationElement[][]{
                                        {
                                                new IeShortFloat(val),
                                                new IeChecksum(0)
                                        }
                                }
                        )
                }
        );


        return asduBack;
    }

    /**
     * 生成电流 ASDU
     *
     * @param addr
     * @param val
     * @param active
     * @return
     */
    private ASdu genCurrent(int addr, StateCurrent val, boolean active) {
        CauseOfTransmission cause;

        if (active) {
            cause = CauseOfTransmission.SPONTANEOUS;
        } else {
            cause = CauseOfTransmission.INTERROGATED_BY_STATION;
        }

        ASdu asduBack = new ASdu(
                TypeId.M_ME_NC_1,           //M_ME_TF_1,
                1,
                cause,
                false,
                false,
                0,
                iec60870Server.Iec60870Addr,
                new InformationObject[]{
                        new InformationObject(
                                addr,
                                new InformationElement[][]{
                                        {
                                                new IeShortFloat(val.A),
                                                new IeChecksum(0)
                                        }
                                }
                        ),
                        new InformationObject(
                                addr + 1,
                                new InformationElement[][]{
                                        {
                                                new IeShortFloat(val.B),
                                                new IeChecksum(0)
                                        }
                                }
                        ),
                        new InformationObject(
                                addr + 2,
                                new InformationElement[][]{
                                        {
                                                new IeShortFloat(val.C),
                                                new IeChecksum(0)
                                        }
                                }
                        ),
                        new InformationObject(
                                addr + 3,
                                new InformationElement[][]{
                                        {
                                                new IeShortFloat(val.O),
                                                new IeChecksum(0)
                                        }
                                }
                        )
                }
        );


        return asduBack;
    }

    /**
     * 生成光纤测温告警 ASDU
     *
     * @param addr
     * @param val
     * @param active
     * @return
     */
    private ASdu genFiber(int addr, StateFiber val, boolean active) {
        return null;
    }


    /**
     * @param devRuntime 设备运行状态对象
     * @param active     是否为主动发送
     */
    private void sendDeviceState(DeviceRuntimeBean devRuntime, boolean active) {
        boolean state = false;
        float val = 0;
        Integer addr;
        int kg = 0;

        ASdu aSdu;      // 需要发送的ASDU

        switch (devRuntime.device.getType()) {
            case 101:           // 开关
                // 发送开关状态
//                if (((StateOutput) devRuntime.runtime.state).output == (Integer) STATE_ON) {
//                    state = true;
//                } else {
//                    state = false;
//                }
//
//                addr = devRuntime.device.getId() + C_DeviceAddr.CONTROL_ADDR;
//                aSdu = genState(addr, state, active);
//
//                try {
//                    connection.send(aSdu);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                // 发送反馈状态
                if (((StateOutput) devRuntime.runtime.state).feedback == (Integer) STATE_ON) {
                    state = true;
                } else {
                    state = false;
                }

                addr = devRuntime.device.getId() + C_DeviceAddr.RUN_STATE;
                aSdu = genState(addr, state, active);

                try {
                    connection.send(aSdu);
                } catch (IOException e) {
                    // e.printStackTrace();
                }


                // 发送远程就地
                if (((StateOutput) devRuntime.runtime.state).remote == (Integer) STATE_ON) {
                    state = true;
                } else {
                    state = false;
                }

                addr = devRuntime.device.getId() + C_DeviceAddr.RUN_MODE;
                aSdu = genState(addr, state, active);

                try {
                    connection.send(aSdu);
                } catch (IOException e) {
                    // e.printStackTrace();
                }

                break;
            case 102:           // 遥信
                // 发送远程就地
                if ((Boolean) devRuntime.runtime.state == Boolean.TRUE) {
                    state = true;
                } else {
                    state = false;
                }

                addr = devRuntime.device.getId() + C_DeviceAddr.STATE_ADDR;
                aSdu = genState(addr, state, active);

                try {
                    connection.send(aSdu);
                } catch (IOException e) {
                    // e.printStackTrace();
                }
                break;
            case 201:           // 遥测
                val = ((StateAnalog) devRuntime.runtime.state).value;
                addr = devRuntime.device.getId() + C_DeviceAddr.MEASURE_ADDR;
                aSdu = genMeasure(addr, val, active);

                try {
                    connection.send(aSdu);
                } catch (IOException e) {
                    // e.printStackTrace();
                }
                break;
            case 301:           // 空调
                break;
            case 501:           // 电流
                StateCurrent stateCurrent = (StateCurrent) devRuntime.runtime.state;
                addr = iec60870Server.addrConvert.getBase104Addr(devRuntime.device.getId());
                aSdu = genCurrent(addr, stateCurrent, active);
                try {
                    connection.send(aSdu);
                } catch (IOException e) {
                    // e.printStackTrace();
                }
                break;
            case 502:           // 光纤告警

//                StateFiber stateFiber = (StateFiber) devRuntime.runtime.state;
//                addr = addrConvert.getBase104Addr(devRuntime.device.getId());
//                aSdu = genFiber(addr, stateFiber, active);
//                try {
//                    connection.send(aSdu);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
        }
    }

    /**
     * 响应总召唤，返回所有数据
     * 1、站召唤应答
     * 2、单个设备信息
     * 3、站召唤结束
     */
    private void sendAllData() {


        // 站召应答
        try {
            connection.interrogation(
                    iec60870Server.Iec60870Addr,
                    CauseOfTransmission.ACTIVATION_CON,
                    new IeQualifierOfInterrogation(0x14)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 循环发送每个设备的状态
        for (DeviceRuntimeBean deviceRuntimeBean : iec60870Server.deviceManager.getDeviceRuntimeList().values()) {
            sendDeviceState(deviceRuntimeBean, false);
        }

        // 站召唤结束
        try {
            connection.interrogation(
                    iec60870Server.Iec60870Addr,
                    CauseOfTransmission.ACTIVATION_TERMINATION,
                    new IeQualifierOfInterrogation(0x14)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param deviceRuntimeBean
     */
    public void updateDevice(DeviceRuntimeBean deviceRuntimeBean) {
        sendDeviceState(deviceRuntimeBean, true);
    }

    /**
     * TODO 遥控
     *
     * @param addr  60870 地址，需要转换
     * @param state 遥控值
     */
    public void devControl(int addr, boolean state) {
        int deviceId = addr - C_DeviceAddr.CONTROL_ADDR;
        iec60870Server.devCtl(deviceId, state);

        ASdu aSdu;      // 需要发送的ASDU

        aSdu = new ASdu(
                TypeId.C_SC_NA_1,
                0x81,
                CauseOfTransmission.ACTIVATION_CON,
                false,
                false,
                0,
                iec60870Server.Iec60870Addr,
                new InformationObject[]{
                        new InformationObject(
                                addr,
                                new InformationElement[][]{
                                        {
                                                new IeChecksum(1)
                                        }
                                })
                }
        );

        // 发送数据
        try {
            connection.send(aSdu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

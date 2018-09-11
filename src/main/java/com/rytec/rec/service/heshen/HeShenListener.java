package com.rytec.rec.service.heshen;

import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.service.iec60870.C_DeviceAddr;
import com.rytec.rec.service.iec60870.Iec60870Listener;
import org.openmuc.j60870.*;

import java.io.IOException;

public class HeShenListener extends Iec60870Listener {

    public HeShenListener(Connection connection, int connectionId) {
        super(connection, connectionId);
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
                setReady(true);
                break;
            case C_CS_NA_1:             // 对时命令
                setReady(false);
                IeTime56 time56 = (IeTime56) aSdu.getInformationObjects()[0].getInformationElements()[0][0];
                sendTimeSync(time56);
                synchronized (connection) {
                    connection.waitForS = true;
                }
                setReady(true);
                break;
            case C_SC_NA_1:             // 遥控
                setReady(false);
                InformationObject io = aSdu.getInformationObjects()[0];
                int addr = io.getInformationObjectAddress();
                int val = ((IeSingleCommand) (io.getInformationElements()[0][0])).getValue();
                processCtl(addr, val);
                setReady(true);
                break;
            default:
                debug("Got unknown request: " + aSdu + ". Will not confirm it.\n");
        }
    }

    @Override
    public void devControl(int addr, boolean state) {
        int deviceId = addr - C_DeviceAddr.CONTROL_ADDR;
        iec60870Server.devCtl(deviceId, state);
    }

    /**
     * 处理控制命令
     * 传送原因
     * CauseOfTransmission.ACTIVATION_CON 可以成功执行
     * CauseOfTransmission.ACTIVATION_TERMINATION 不能成功执行
     *
     * @param val
     */
    private void processCtl(int addr, int val) {
        ASdu asduBack;
        asduBack = new ASdu(
                TypeId.C_SC_NA_1,
                1,
                CauseOfTransmission.ACTIVATION_CON,
                false, false,
                0,
                iec60870Server.Iec60870Addr,
                new InformationObject[]{
                        new InformationObject(
                                0x6001,
                                new InformationElement[][]{
                                        {
                                                new IeChecksum(val)
                                        }
                                })
                }
        );
        try {
            connection.send(asduBack);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 执行操作
        switch (val) {
            case 0x80:      // 预分
                break;
            case 0x81:      // 预合
                break;
            case 0x00:      // 分
                devControl(addr, false);
                break;
            case 0x01:      // 合
                devControl(addr, true);
                break;
        }
    }

    @Override
    public void updateDevice(DeviceRuntimeBean deviceRuntimeBean) {
        if (!ready || connection.waitForS) {
            return;
        }
        connection.waitForS = true;
        sendDeviceState(deviceRuntimeBean, true);
    }

    // 设置 Ready 的值
    protected synchronized void setReady(boolean v) {
        ready = v;
    }
}

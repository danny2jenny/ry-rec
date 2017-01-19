package com.rytec.rec.device.devices;


import com.rytec.rec.device.BaseDevice;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.*;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-11-29.
 * 输出
 * 三个端口：
 * 101:control：开启、关闭
 * 102:feedback：辅助节点，判断开启、关闭是否成功
 * 103:remote：本地、远程
 */
@Service
@DeviceType(101)
public class DeviceOutput extends BaseDevice {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 开关控制
     *
     * @param deviceId
     * @param from
     * @param value
     */
    public int setSwitch(int deviceId, int from, Boolean value) {
        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = from;
        nodeMsg.type = CommandType.GENERAL_WRITE;
        nodeMsg.value = value;
        return this.setValue(deviceId, DeviceFunctionType.DEV_FUN_PORT_MAIN, nodeMsg);
    }

}

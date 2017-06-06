package com.rytec.rec.device.operator;

import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.util.ConstantMessageType;
import com.rytec.rec.util.AnnotationDeviceType;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.ConstantDeviceState;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-12-16.
 * 输入设备
 * <p>
 * 信号：
 * 1、开
 * 2、关
 */
@Service
@AnnotationDeviceType(102)
@AnnotationJSExport("开关输入")
public class Input extends AbstractOperator {

    /**
     * 常量的列表，表示该设备可以输出那些信号
     */
    @AnnotationJSExport("开启")
    public static int SIG_ON = 10;           //开启信号1

    @AnnotationJSExport("关闭")
    public static int SIG_OFF = -10;          //关闭信号0


    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue, String unit) {

        // 得到运行状态
        DeviceRuntimeBean deviceRuntimeBean = deviceManager.deviceRuntimeList.get(deviceId);
        deviceRuntimeBean.runtime.state = newValue;

        // 处理掉线
        if (newValue == null) {
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_OFFLINE;
            // 向客户端广播消息
            clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
            return;
        }

        if ((Boolean) newValue == true) {
            // 告警
            sendSig(deviceId, Input.SIG_ON, null);
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ON;
            sendAlarm(deviceId, Input.SIG_ON, null);
        } else {
            // 告警恢复
            sendSig(deviceId, Input.SIG_OFF, null);
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_OFF;
        }

        // 向客户端广播消息
        clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
    }


}

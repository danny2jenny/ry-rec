package com.rytec.rec.device.operator;

import com.rytec.rec.channel.KhFiber.FiberVal;
import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.device.state.StateFiber;
import com.rytec.rec.util.AnnotationDeviceType;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.ConstantDeviceState;
import com.rytec.rec.util.ConstantMessageType;
import org.springframework.stereotype.Service;

@Service
@AnnotationDeviceType(502)
@AnnotationJSExport("光纤测温")
public class Fiber extends AbstractOperator {

    @AnnotationJSExport("高温预警")
    public static int SIG_PRE_ALM = 1;

    @AnnotationJSExport("高温告警")
    public static int SIG_ALM = 2;

    @AnnotationJSExport("温差告警")
    public static int SIG_DELTA = 3;

    /**
     * 状态改变
     *
     * @param deviceId
     * @param fun
     * @param oldValue
     * @param newValue
     * @param unit
     */
    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue, String unit) {
        // 得到运行状态
        DeviceRuntimeBean deviceRuntimeBean = deviceManager.getDeviceRuntimeList().get(deviceId);

        if (newValue == null) {
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_OFFLINE;
            return;
        }

        FiberVal val = (FiberVal) newValue;

        ((StateFiber) deviceRuntimeBean.runtime.state).type = val.type;
        ((StateFiber) deviceRuntimeBean.runtime.state).position = val.position;
        ((StateFiber) deviceRuntimeBean.runtime.state).val = val.value;

        deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ON;

        // 告警处理
        sendSig(deviceId, val.type, val.value);
        sendAlarm(deviceId, val.type, val.value);

        // 发广播
        clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
    }

    @Override
    public Object generateStateBean() {
        return new StateFiber();
    }
}

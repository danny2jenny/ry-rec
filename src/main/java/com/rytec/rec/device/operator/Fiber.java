package com.rytec.rec.device.operator;

import com.rytec.rec.channel.KhFiber.FiberVal;
import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.device.state.StateFiber;
import com.rytec.rec.service.SMS;
import com.rytec.rec.util.AnnotationDeviceType;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.ConstantDeviceState;
import com.rytec.rec.util.ConstantMessageType;
import org.springframework.beans.factory.annotation.Autowired;
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

    String smsFmt = "%s:%d米处%s!温度值:%.1f";

    @Autowired
    SMS sms;

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

        if (val.type > 0) {
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ALM;
            // 告警处理
            sendSig(deviceId, val.type, val.value);
            sendAlarm(deviceId, val.type, val.value);

            // 短消息告警处理
            String smsBody = "";
            switch (val.type) {
                case 1:
                    smsBody = String.format(
                            smsFmt, deviceRuntimeBean.device.getName(),
                            val.position,
                            "高温预警",
                            val.value
                    );
                    sms.fiberSms(smsBody);
                    break;
                case 2:
                    smsBody = String.format(
                            smsFmt, deviceRuntimeBean.device.getName(),
                            val.position,
                            "高温告警",
                            val.value
                    );
                    sms.fiberSms(smsBody);
                    break;
                case 3:
                    smsBody = String.format(
                            smsFmt, deviceRuntimeBean.device.getName(),
                            val.position,
                            "温差告警",
                            val.value
                    );
                    break;
            }


        } else {
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ON;
        }

        // 发广播
        clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
    }

    @Override
    public Object generateStateBean() {
        return new StateFiber();
    }
}

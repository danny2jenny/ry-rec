package com.rytec.rec.device.operator;

import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.device.state.StateCurrent;
import com.rytec.rec.util.AnnotationDeviceType;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.ConstantDeviceState;
import com.rytec.rec.util.ConstantMessageType;
import org.springframework.stereotype.Service;

@Service
@AnnotationDeviceType(501)
@AnnotationJSExport("环流")
public class Current extends AbstractOperator {

    /**
     * 设备的状态改变
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

        float[] val = (float[]) newValue;

        ((StateCurrent) deviceRuntimeBean.runtime.state).A = val[0];
        ((StateCurrent) deviceRuntimeBean.runtime.state).B = val[1];
        ((StateCurrent) deviceRuntimeBean.runtime.state).C = val[2];
        ((StateCurrent) deviceRuntimeBean.runtime.state).O = val[3];
        deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ON;

        // 发广播
        clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
    }

    @Override
    public Object generateStateBean() {
        return new StateCurrent();
    }
}

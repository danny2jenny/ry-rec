package com.rytec.rec.device.operator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.device.config.AnalogConfig;
import com.rytec.rec.util.ConstantMessageType;
import com.rytec.rec.util.ConstantDeviceState;
import com.rytec.rec.util.AnnotationDeviceType;
import com.rytec.rec.util.AnnotationJSExport;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by danny on 16-11-29.
 * 模拟量
 * 一个端口只读
 * <p>
 * 信号：
 * 1、超限（A）
 * 2、超限（B）
 * 3、超限（C）
 */
@Service
@AnnotationDeviceType(201)
@AnnotationJSExport("模拟输入")
public class Analog extends AbstractOperator {

    @AnnotationJSExport("+高限告警")
    public static int SIG_HIGH_2 = 20;

    @AnnotationJSExport("+高限联动")
    public static int SIG_HIGH_1 = 10;

    @AnnotationJSExport("o正常")
    public static int SIG_NORMAL = 1;

    @AnnotationJSExport("-低限联动")
    public static int SIG_LOW_1 = -10;

    @AnnotationJSExport("-低限告警")
    public static int SIG_LOW_2 = -20;

    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue, String unit) {

        // 得到运行状态，更新状态
        DeviceRuntimeBean deviceRuntimeBean = deviceManager.deviceRuntimeList.get(deviceId);
        ((StateAnalog) deviceRuntimeBean.runtime.state).unit = unit;

        if (newValue == null) {
            ((StateAnalog) deviceRuntimeBean.runtime.state).value = 0;
        } else {
            ((StateAnalog) deviceRuntimeBean.runtime.state).value = (Float) newValue;
        }


        // 处理掉线
        if (newValue == null) {
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_OFFLINE;
            // 向客户端广播消息
            clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
            return;
        }

        deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ON;


        // 判断当前的值是否需要发送信号
        AnalogConfig config = (AnalogConfig) getConfig(deviceId);


        if ((Float) newValue >= config.GATE_HIGH_2) {

            // 高限告警
            sendSig(deviceId, Analog.SIG_HIGH_2, newValue);
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ALM;
            sendAlarm(deviceId, Input.SIG_ON, newValue);
        } else if ((Float) newValue >= config.GATE_HIGH_1) {
            // 高限联动
            sendSig(deviceId, Analog.SIG_HIGH_1, newValue);

        } else if ((Float) newValue >= config.GATE_LOW_1) {
            // 正常范围
            sendSig(deviceId, Analog.SIG_NORMAL, newValue);

        } else if ((Float) newValue >= config.GATE_LOW_2) {
            // 低限联动
            sendSig(deviceId, Analog.SIG_LOW_1, newValue);

        } else {
            // 低限告警
            sendSig(deviceId, Analog.SIG_LOW_2, newValue);
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ALM;
            sendAlarm(deviceId, Input.SIG_ON, newValue);
        }

        // 发广播
        clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
    }

    @Override
    public Object parseConfig(String config) {
        AnalogConfig analogConfig;
        try {
            analogConfig = new ObjectMapper().readValue(config, AnalogConfig.class);
        } catch (IOException e) {
            analogConfig = new AnalogConfig();
        }
        return analogConfig;
    }

    @Override
    public Object generateStateBean() {
        return new StateAnalog();
    }
}

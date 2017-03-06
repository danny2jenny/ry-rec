package com.rytec.rec.device.operator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceRuntimeConfigBean;
import com.rytec.rec.device.config.AnalogConfig;
import com.rytec.rec.messenger.MessageType;
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
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue) {

        // 得到运行状态
        DeviceRuntimeConfigBean deviceRuntimeConfigBean = deviceManager.deviceRuntimeList.get(deviceId);

        deviceRuntimeConfigBean.runtime.iconState = ConstantDeviceState.STATE_ON;
        deviceRuntimeConfigBean.runtime.state = newValue;

        // 判断当前的值是否需要发送信号
        AnalogConfig config = (AnalogConfig) getConfig(deviceId);


        if ((Float) newValue >= config.GATE_HIGH_2) {

            // 高限告警
            sendSig(deviceId, Analog.SIG_HIGH_2, newValue);
            deviceRuntimeConfigBean.runtime.iconState = ConstantDeviceState.STATE_ALM;
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
            deviceRuntimeConfigBean.runtime.iconState = ConstantDeviceState.STATE_ALM;
        }

        // 发广播
        clientBroadcast(MessageType.DEVICE_STATE, deviceRuntimeConfigBean);
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
}

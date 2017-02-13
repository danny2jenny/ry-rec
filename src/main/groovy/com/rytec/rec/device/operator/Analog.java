package com.rytec.rec.device.operator;

import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.util.ConstantDeviceState;
import com.rytec.rec.util.Description;
import com.rytec.rec.util.DeviceType;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-11-29.
 * 模拟量
 * 一个端口只读
 *
 * 信号：
 * 1、超限（A）
 * 2、超限（B）
 * 3、超限（C）
 */
@Service
@DeviceType(201)
@Description("模拟输入")
public class Analog extends AbstractOperator {

    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue) {
        setState(deviceId, ConstantDeviceState.STATE_ON);
    }
}

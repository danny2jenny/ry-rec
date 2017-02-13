package com.rytec.rec.device.operator;

import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.util.Description;
import com.rytec.rec.util.DeviceType;
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
@DeviceType(102)
@Description("开关输入")
public class Input extends AbstractOperator {

    /**
     * 常量的列表，表示该设备可以输出那些信号
     */
    @Description("开启")
    public static int SIG_ON = 1;           //开启信号1

    @Description("关闭")
    public static int SIG_OFF = 0;          //关闭信号0


    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue) {

        if ((Boolean) newValue == true) {
            setState(deviceId, ConstantDeviceState.STATE_ON);
        } else {
            setState(deviceId, ConstantDeviceState.STATE_OFF);
        }

    }


}

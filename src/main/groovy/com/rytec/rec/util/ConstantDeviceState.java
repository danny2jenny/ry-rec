package com.rytec.rec.util;

/**
 * Created by danny on 17-1-23.
 * <p>
 * Feature 状态
 */

@JSExport("设备状态")
@Description("DEVICE_STATE")
public interface ConstantDeviceState {
    @JSExport("不可用")
    int STATE_INAVAILABLE = 10;
    @JSExport("正常状态")
    int STATE_NORMAL = 11;
    @JSExport("工作状态")
    int STATE_OFF = 20;
    @JSExport("停止状态")
    int STATE_ON = 21;
}

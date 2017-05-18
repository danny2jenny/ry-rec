package com.rytec.rec.device;

import com.rytec.rec.util.ConstantDeviceState;

/**
 * Created by danny on 17-1-25.
 * <p>
 * Device 的状态
 */
public class DeviceStateBean {
    public int device;                                                      // 设备编号
    public int iconState = ConstantDeviceState.STATE_OFFLINE;               // 图标
    public Object state;                                                    // 状态值，可以是单个值也可以是个对象
}

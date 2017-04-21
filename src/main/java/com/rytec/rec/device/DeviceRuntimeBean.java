package com.rytec.rec.device;

import com.rytec.rec.db.model.Device;

/**
 * Created by danny on 17-1-25.
 * <p>
 * Device 运行时的描述
 */
public class DeviceRuntimeBean {
    public Device device;                //Device ID
    public DeviceStateBean runtime;      //Device 状态
    public Object config;                //Device 的配置对象
}

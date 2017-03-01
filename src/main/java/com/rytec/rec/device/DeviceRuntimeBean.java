package com.rytec.rec.device;

import com.rytec.rec.db.model.Device;

/**
 * Created by danny on 17-1-25.
 * <p>
 * Device 运行时的描述
 */
public class DeviceRuntimeBean {
    Device device;                  //Device ID
    DeviceStateBean state;          //Device 状态
    Object config;                  //Device 的配置对象
}

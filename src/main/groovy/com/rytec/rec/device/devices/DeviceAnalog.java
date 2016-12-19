package com.rytec.rec.device.devices;

import com.rytec.rec.device.BaseDevice;
import com.rytec.rec.util.DeviceType;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-11-29.
 * 模拟量
 * 一个端口只读
 */
@Service
@DeviceType(201)
public class DeviceAnalog extends BaseDevice {
}

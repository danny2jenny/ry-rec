package com.rytec.rec.device;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-11-14.
 * 通过这个接口和前端设备进行通信
 * 这里会根据前端设备的类型去调用相应的Device驱动
 * <p>
 * 这是所有与设备操作的入口
 */
@Service
public class DeviceManager {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
    * 前端设备数据变化
    * Aircon: 设备id
    * fun：设备功能号
    * oldValue：原值
    * newValue：新值
    */
    public void onValueChange(int device, int fun, float oldValue, float newValue) {

    }
}

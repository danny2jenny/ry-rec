package com.rytec.rec.device;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by danny on 16-11-21.
 * 这个是Device的基类
 */
public class BaseDevice {

    @Autowired
    DeviceManager deviceManager;

    /*
    * 设置输出的值
    */
    public void setValue(int deviceId, int fun, int value) {



    }

    /*
    * 当状态改变时，由通讯层的回调
    */
    public void onValueChanged(int deviceId, int fun, int value) {

    }

}

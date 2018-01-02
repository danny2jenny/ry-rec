package com.rytec.rec.service.iec60870;

public interface C_DeviceAddr {

    int DIGITAL_ADDR = 0;
    int DIGITAL_STATE = 1000;       // 运行状态
    int DIGITAL_MODE = 2000;        // 运行模式
    int ERROR_ADDR = 3000;          // 告警或者是错误
    int ANALOG_ADDR = 4000;
    int CONTROL_ADDR = 5000;
    int ADJUST_ADDR = 6000;
}

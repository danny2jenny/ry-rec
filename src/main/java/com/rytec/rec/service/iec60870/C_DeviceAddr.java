package com.rytec.rec.service.iec60870;

/**
 * 遥信∶ 1H~4000H             (1~16384)
 * 遥测∶ 4001H~5000H          (16385~20480)
 * 参数地址： 5001H~6000H       (20481~24576)
 * 遥控、升降地址： 6001H~7000H (24577~28672)
 */
public interface C_DeviceAddr {

    int STATE_ADDR = 0;             // 遥信地址
    int MEASURE_ADDR = 16385;       // 遥测地址
    int CONTROL_ADDR = 24577;       // 遥控地址

    int RUN_STATE = 4000;           // 运行状态
    int RUN_MODE = 8000;            // 运行模式
    int CRT_FIBER = 18000;          // 电流传感，光纤地址

}

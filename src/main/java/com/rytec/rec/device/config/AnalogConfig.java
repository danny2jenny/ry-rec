package com.rytec.rec.device.config;

/**
 * Created by Danny on 2017/2/24.
 * 模拟量输入的配置对象
 */
public class AnalogConfig {
    public float GATE_HIGH_2 = 99999999;   // 最高门限
    public float GATE_HIGH_1 = 55555555;   // 高门限
    public float GATE_LOW_1 = -55555555;   // 低门限
    public float GATE_LOW_2 = -99999999;   // 最低门限
}

package com.rytec.rec.util;

/**
 * Created by danny on 16-12-16.
 * 一个设备的端口配置
 */

@JSExport("DEVICE_FUN")
public interface ConstantDeviceFunction {

    @JSExport("缺省端口")
    int DEV_FUN_PORT_MAIN = 101;    //主端口，用于状态读取，控制
    @JSExport("反馈端口")
    int DEV_FUN_PORT_FEDBK = 102;   //反馈端口
    @JSExport("远程就地切换端口")
    int DEV_FUN_PORT_RMOT = 103;    //本地\远程切换端口
}

package com.rytec.rec.util;

/**
 * Created by danny on 16-12-16.
 * 一个设备的端口配置
 */

@AnnotationJSExport("DEVICE_FUN")
public interface ConstantDeviceFunction {

    @AnnotationJSExport("缺省端口")
    int DEV_FUN_PORT_MAIN = 101;
    @AnnotationJSExport("反馈端口")
    int DEV_FUN_PORT_FEDBK = 102;
    @AnnotationJSExport("远程就地切换端口")
    int DEV_FUN_PORT_RMOT = 103;
}

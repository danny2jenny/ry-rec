package com.rytec.rec.util;

/**
 * Created by danny on 16-12-16.
 * 一个设备的端口配置
 */

@AnnotationJSExport("DEVICE_FUN")
public interface ConstantDeviceFunction {
    @AnnotationJSExport("1-缺省")
    int DEV_FUN_PORT_A = 101;
    @AnnotationJSExport("2-反馈")
    int DEV_FUN_PORT_B = 102;
    @AnnotationJSExport("3-切换")
    int DEV_FUN_PORT_C = 103;
    @AnnotationJSExport("4-备用")
    int DEV_FUN_PORT_D = 104;
}

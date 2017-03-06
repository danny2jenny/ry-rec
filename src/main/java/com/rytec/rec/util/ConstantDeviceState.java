package com.rytec.rec.util;

/**
 * Created by danny on 17-1-23.
 * <p>
 * Feature 状态
 */

@AnnotationJSExport("DEVICE_STATE")
public interface ConstantDeviceState {

    // 不在线
    @AnnotationJSExport("不可用")
    int STATE_OFFLINE = 10;

    // 用于普通的无状态的显示，或者叫做普通状态
    @AnnotationJSExport("正常状态")
    int STATE_NORMAL = 11;

    // 开启，或是运行中
    @AnnotationJSExport("停止状态")
    int STATE_OFF = 20;

    // 关闭
    @AnnotationJSExport("工作状态")
    int STATE_ON = 21;

    // 告警
    @AnnotationJSExport("告警状态")
    int STATE_ALM = 31;


}

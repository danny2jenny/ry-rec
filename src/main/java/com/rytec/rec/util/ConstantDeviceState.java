package com.rytec.rec.util;

/**
 * Created by danny on 17-1-23.
 * <p>
 * Feature 状态
 */

@AnnotationJSExport("DEVICE_STATE")
public interface ConstantDeviceState {
    @AnnotationJSExport("不可用")
    int STATE_INAVAILABLE = 10;
    @AnnotationJSExport("正常状态")
    int STATE_NORMAL = 11;
    @AnnotationJSExport("工作状态")
    int STATE_OFF = 20;
    @AnnotationJSExport("停止状态")
    int STATE_ON = 21;
}

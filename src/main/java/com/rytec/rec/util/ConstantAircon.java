package com.rytec.rec.util;

/**
 * 空调状态
 */

@AnnotationJSExport("DEV_STATE_AIRCON")
public interface ConstantAircon {

    @AnnotationJSExport("停止")
    int STATE_STOP = 0;

    @AnnotationJSExport("制冷")
    int STATE_COLD = 1;

    @AnnotationJSExport("制热")
    int STATE_HOT = 2;
}

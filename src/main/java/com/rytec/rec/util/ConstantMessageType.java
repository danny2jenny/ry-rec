package com.rytec.rec.util;

import com.rytec.rec.util.AnnotationJSExport;

/**
 * Created by danny on 17-1-25.
 * 消息类型
 */
@AnnotationJSExport("MSG_TYPE")
public interface ConstantMessageType {

    @AnnotationJSExport("设备状态改变")
    int DEVICE_STATE = 401;         // 设备状态消息

    @AnnotationJSExport("节点状态改变")
    int NODE_STATE = 501;           // 节点状态消息

    @AnnotationJSExport("通道状态改变")
    int CHANNEL_STATE = 601;        // 通道状态消息

    @AnnotationJSExport("设备告警")
    int DEVICE_ALARM = 1000;        // 设备告警
}

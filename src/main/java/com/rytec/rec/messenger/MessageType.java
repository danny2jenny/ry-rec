package com.rytec.rec.messenger;

/**
 * Created by danny on 17-1-25.
 * 消息类型
 */
public interface MessageType {

    int DEVICE_STATE = 401;         // 设备状态消息
    int NODE_STATE = 501;           // 节点状态消息
    int CHANNEL_STATE = 601;        // 通道状态消息
}

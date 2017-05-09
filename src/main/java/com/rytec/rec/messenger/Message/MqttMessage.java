package com.rytec.rec.messenger.Message;

/**
 * Created by danny on 17-5-6.
 * Mqtt 间的消息，Json
 */
public class MqttMessage {
    public int cmd;                 // 命令
    public Object payload;          // 消息体
}

package com.rytec.rec.channel.ModbusTcpServer.entity;

import io.netty.buffer.ByteBuf;

/**
 * Created by danny on 16-12-12.
 * 所有帧的抽象描述
 * 只用于channel保证数据帧的收发，不判断内容
 */
public class ModbusFrame {
    public byte type;          //0 代表流出，1代表流入, 100 代表登录消息
    public int responseLen;    //返回帧的长度
    public ByteBuf payload;    //有效数据，发送的或者是接收的
}

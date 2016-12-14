package com.rytec.rec.channel.ModbusTcpServer;

/**
 * Created by danny on 16-12-14.
 * 每个TCP连接对应Channel的状态
 */
public class ChannelState {
    public int state;       //状态 0 空闲， 1 系统通讯 2 联动通讯 3 用户通讯
    public int expectLen;   //期望返回的长度

    public ChannelState() {
        state = 0;
    }
}

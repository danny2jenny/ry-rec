package com.rytec.rec.util;

/**
 * Created by Danny on 2017/3/14.
 * 用于TCP通信的命令
 */
public interface ConstantTcpCommand {
    short TCP_SEND_CFG = 1;       // 发送配置
    short TCP_SEND_ACT = 2;       // 发送动作命令
}

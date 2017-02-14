package com.rytec.rec.util;

/**
 * Created by danny on 16-12-14.
 */
public interface ConstantFromWhere {

    // 系统外发命令
    int FROM_TIMER = 1;     //系统定时命令
    int FROM_ALI = 2;       //联动
    int FROM_USER = 3;      //用户

    // 远端的设备发送
    int FROM_LOGIN = 1000;  //远端登录
    int FROM_RPS = 1001;    //远端应答
}

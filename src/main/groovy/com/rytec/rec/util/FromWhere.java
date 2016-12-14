package com.rytec.rec.util;

/**
 * Created by danny on 16-12-14.
 */
public interface FromWhere {
    int FROM_SYS = 1;       //系统
    int FROM_ALI = 2;       //联动
    int FRM_USER = 3;       //用户

    int FROM_LOG = 1000;    //远端登录
    int FROM_RPS = 1001;    //远端应答
}

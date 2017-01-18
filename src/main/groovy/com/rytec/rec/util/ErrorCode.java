package com.rytec.rec.util;

/**
 * Created by danny on 17-1-17.
 * <p>
 * 返回值的含义
 */
public interface ErrorCode {

    int RST_SUCCESS = 0;


    // DEVICE 操作的错误代码

    int DEVICE_NOT_FOUND = 101;             //设备未找到
    int DEVICE_FUN_NOT_CONFIG = 102;        //设备的该功能未配置
    int DEVICE_FUN_NOT_EXIST = 103;         //设备没有该功能

    // Node通讯的错误列表
    int NODE_TYPE_NOTEXIST = 201;           //该类型的Node不存在


}

package com.rytec.rec.util;

/**
 * Created by danny on 17-3-28.
 * <p>
 * 视频通讯的常量
 */
public interface ConstantMqtt {

    // Video 相关的命令和 C 的服务器对应
    int VIDEO_INIT = 1;
    int VIDEO_PTZ = 2;
    int VIDEO_INIT_REQUEST = 11;
    int VIDEO_CHANNEL_ONLINE = 21;

    // 61850 相关命令，和C 服务器对应
    int IEC61850_INIT = 101;
    int IEC61850_UPDATE = 102;
    int IEC61850_INIT_REQUEST = 111;

    int IEC61850_SWITCH = 120;


    // Video 相关控制
    int PTZ_SET_PRESET = 1;     //设置
    int PTZ_CLE_PRESET = 2;     //清除
    int PTZ_GOTO_PRESET = 3;    //调用

    //TOPC
    String TOPIC_VIDEO_SERVICE = "rec/srv/video/to";
}

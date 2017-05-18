package com.rytec.rec.util;

/**
 * Created by danny on 17-3-28.
 * <p>
 * 视频通讯的常量
 */
public interface ConstantVideo {

    // 和 C 的服务器对应
    int VIDEO_INIT = 1;
    int VIDEO_PTZ = 2;

    int VIDEO_INIT_REQUEST = 101;
    int VIDEO_INIT_INFO = 102;

    int VIDEO_CHANNEL_ONLINE = 201;


    int PTZ_SET_PRESET = 1;     //设置
    int PTZ_CLE_PRESET = 2;     //清除
    int PTZ_GOTO_PRESET = 3;    //调用

    //TOPC
    String TOPIC_VIDEO_SERVICE = "rec/srv/video/to";
}

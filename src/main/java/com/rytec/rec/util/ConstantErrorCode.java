package com.rytec.rec.util;

/**
 * Created by danny on 17-1-17.
 * <p>
 * 返回值的含义
 */

@AnnotationJSExport("ERROR_CODE")
public interface ConstantErrorCode {

    @AnnotationJSExport("成功")
    int RST_SUCCESS = 0;

    @AnnotationJSExport("CRC 错误")
    int REST_CRC_ERROR = 10;

    // DEVICE 操作的错误代码
    @AnnotationJSExport("设备未找到")
    int DEVICE_NOT_FOUND = 101;             //设备未找到
    @AnnotationJSExport("设备的该功能端口未配置")
    int DEVICE_FUN_NOT_CONFIG = 102;        //设备的该功能未配置
    @AnnotationJSExport("设备没有该功能")
    int DEVICE_FUN_NOT_EXIST = 103;         //设备没有该功能
    @AnnotationJSExport("设备没有该动作")
    int DEVICE_ACT_NOT_EXIST = 104;

    // Node通讯的错误列表
    @AnnotationJSExport("该类型的节点不存在")
    int NODE_TYPE_NOTEXIST = 201;           //该类型的Node不存在
    @AnnotationJSExport("发送值不匹配")
    int NODE_VALUE_TYPE = 202;

    // Channel错误消息
    @AnnotationJSExport("通道未连接")
    int CHA_NOT_CONNECT = 301;

    // 联动错误信息
    @AnnotationJSExport("联动规则未设置")
    int RULE_ACTION_NOT_EXIST = 401;
    @AnnotationJSExport("联动动作未设置")
    int RULE_ACTION_NO_ACTION = 402;

    // Service 错误消息
    @AnnotationJSExport("服务未激活")
    int SRV_NOT_ACTIVE = 501;
    @AnnotationJSExport("服务不存在")
    int SRV_NOT_EXIST = 502;


}

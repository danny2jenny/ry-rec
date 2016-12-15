package com.rytec.rec.bean;

/**
 * Created by danny on 16-11-21.
 * <p>
 * channel和node的列表
 */
public class ChannelNode {
    public int id;                 //channel的id
    public String cname;           //channel的名称
    public String ip;              //channel的ip
    public int port;               //channel的端口
    public String login;           //channel的登录名
    public String pass;            //channel的密码
    public int ctype;              //channel的类型
    public String channelConf;     //channel的配置

    public int nid;                //node的id
    public int add;                //node的地址
    public int no;                 //node的编号
    public String nname;           //node的名称
    public int ntype;              //node的类型
    public String nodeConf;        //node的配置
    public int device;             //node对应的设备编号
    public int deviceFun;          //node对应的设备功能

    // ---------------非数据库字段，用于业务逻辑保存临时信息-------------------
    public Object opt;             //其他配置信息


}

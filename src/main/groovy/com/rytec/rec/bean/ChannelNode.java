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
    public int no;                 //node的编号
    public String nname;           //node的名称
    public int ntype;              //node的类型
    public String nodeConf;        //node的配置
    public int device;             //node对应的设备编号
    public int deviceFun;          //node对应的设备功能

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getCtype() {
        return ctype;
    }

    public void setCtype(int ctype) {
        this.ctype = ctype;
    }

    public String getChannelConf() {
        return channelConf;
    }

    public void setChannelConf(String channelConf) {
        this.channelConf = channelConf;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getNname() {
        return nname;
    }

    public void setNname(String nname) {
        this.nname = nname;
    }

    public int getNtype() {
        return ntype;
    }

    public void setNtype(int ntype) {
        this.ntype = ntype;
    }

    public String getNodeConf() {
        return nodeConf;
    }

    public void setNodeConf(String nodeConf) {
        this.nodeConf = nodeConf;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public int getDeviceFun() {
        return deviceFun;
    }

    public void setDeviceFun(int deviceFun) {
        this.deviceFun = deviceFun;
    }
}

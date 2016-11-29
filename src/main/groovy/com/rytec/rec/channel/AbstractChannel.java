package com.rytec.rec.channel;

/**
 * Created by danny on 16-11-17.
 * 通讯通道的抽象类
 * 1、建立通讯对象
 * 2、如何发送数据
 * 3、如何接收数据
 * 4、数据如何分包
 * 5、连接状态
 * <p>
 * 一个Channel可以管理多个Channel的连接对象
 */
public interface AbstractChannel {

    //初始化接口
    public void initialization();

    public void getConfig();

    public Object getChannel(String id);
}

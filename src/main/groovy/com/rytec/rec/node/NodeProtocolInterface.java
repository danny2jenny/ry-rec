package com.rytec.rec.node;

/**
 * Created by danny on 16-11-20.
 * Node 的通讯接口
 */
public interface NodeProtocolInterface {

    /*
    * 生成通讯帧
    * where 从哪里来的真 1 系统 2 联动 3 用户
    * add 地址，485 的地址
    * reg 寄存器地址
    * type 帧类型
    */
    Object genMessage(int where, int nodeId, int cmd, int value);

    int decodeMessage(Object msg);
}

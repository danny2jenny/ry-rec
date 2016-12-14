package com.rytec.rec.node;

import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;

/**
 * Created by danny on 16-11-20.
 */
public interface AbstractNode {

    /*
    * 生成通讯帧
    * where 从哪里来的真 1 系统 2 联动 3 用户
    * add 地址，485 的地址
    * reg 寄存器地址
    * type 帧类型
    */
    Object genFrame(int where, int add, int reg, int cmd);
}

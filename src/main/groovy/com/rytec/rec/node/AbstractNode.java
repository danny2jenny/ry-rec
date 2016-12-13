package com.rytec.rec.node;

import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;

/**
 * Created by danny on 16-11-20.
 */
public interface AbstractNode {

    //生成通讯帧
    Object genFrame(int add, int reg, int type);
}

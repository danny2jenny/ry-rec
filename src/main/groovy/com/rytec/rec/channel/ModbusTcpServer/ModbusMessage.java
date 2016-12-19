package com.rytec.rec.channel.ModbusTcpServer;

import com.rytec.rec.util.CommandType;
import com.rytec.rec.util.FromWhere;

/**
 * Created by danny on 16-12-12.
 * 所有帧的抽象描述
 * 只用于channel保证数据帧的收发，不判断内容
 */
public class ModbusMessage {
    public int type = CommandType.MODBUS_CMD_READ;    //命令类型
    public int from = FromWhere.FROM_TIME;      //该命令的触发是哪里
    public int responseLen = 0;                 //返回帧的长度
    public byte[] payload;                      //有效数据，发送的或者是接收的

    public int nodeId;                          //node的id


    public ModbusMessage() {

    }

    public ModbusMessage(int where) {
        from = where;
    }

}

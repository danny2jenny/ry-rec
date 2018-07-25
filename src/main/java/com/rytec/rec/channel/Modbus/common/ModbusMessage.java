package com.rytec.rec.channel.Modbus.common;

import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.buffer.ByteBuf;

/**
 * Created by danny on 17-5-14.
 * <p>
 * Modbus 中的消息
 */
public class ModbusMessage {
    public int type = ConstantCommandType.GENERAL_WRITE;        // 命令类型
    public int from = ConstantFromWhere.FROM_TIMER;             // 该命令的触发是哪里
    public int nodeId;                                          // node的id

    public int regCount = 1;                                    // 寄存器长度

    public int interval;                                        // 发送间隔
    public int intervalCount;                                   // 发送间隔计数

    public int responseLen = 0;                                 // 返回帧的长度
    public ByteBuf payload;                                     // 有效数据，发送的或者是接收的

    public int retry = 0;                                       // 命令重试次数，大于三次后失败
    public int overtime = 0;                                    // 超时计数，不同的Node，不同的对待

    public ModbusMessage() {

    }

    public ModbusMessage(int where) {
        from = where;
    }
}

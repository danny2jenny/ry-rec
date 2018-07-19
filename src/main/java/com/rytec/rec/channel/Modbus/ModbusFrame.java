package com.rytec.rec.channel.Modbus;

import com.rytec.rec.util.CRC16;
import com.rytec.rec.util.ConstantModbusCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by danny on 17-5-12.
 * 生成Modbus的通讯帧
 * <p>
 * Modbus 常用命令
 * 1：读取继电器（线圈）
 * 2：读取开关量输入（离散量）
 * 4：读取模拟量输入（寄存器）
 * 5：写继电器（线圈）
 */
public class ModbusFrame {

    /**
     * 通用读取
     *
     * @param cmd
     * @param adr
     * @param start
     * @param count
     * @return
     */
    static public ByteBuf generalCmd(int cmd, int adr, int start, int count) {
        ByteBuf buf = Unpooled.buffer(6);
        buf.writeByte(adr);
        buf.writeByte(cmd);
        buf.writeShort(start);     //地址
        buf.writeShort(count);
        int crc = CRC16.calcCrc16(buf.array());
        buf.writeShort(crc);
        return buf;
    }

    // 读取继电器（线圈）：1
    static public ByteBuf readCoils(int adr, int start, int count) {
        return generalCmd(ConstantModbusCommand.READ_COILS, adr, start, count);
    }

    // 读取开关量输入：2
    static public ByteBuf readInput(int adr, int start, int count) {
        return generalCmd(ConstantModbusCommand.READ_INPUT, adr, start, count);
    }

    // 读取模拟量:3
    static public ByteBuf readHoldingRegisters(int adr, int start, int count) {
        return generalCmd(ConstantModbusCommand.READ_HOLDING_REGISTERS, adr, start, count);
    }

    // 读取模拟量输入:4
    static public ByteBuf readRegisters(int adr, int start, int count) {
        return generalCmd(ConstantModbusCommand.READ_REGISTERS, adr, start, count);
    }

    // 开关控制，写开关:5
    static public ByteBuf writeCoil(int adr, int reg, int val) {
        return generalCmd(ConstantModbusCommand.WRITE_COIL, adr, reg, val);
    }

    // 写寄存器: 6
    static public ByteBuf writeRegister(int adr, int reg, int val){
        return generalCmd(ConstantModbusCommand.WRITE_REGISTER, adr, reg, val);
    }
}

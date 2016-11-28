package com.rytec.rec.channel.ModbusTcpServer.entity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by danny on 16-11-22.
 */
public class ModbusFrame {

    private final ModbusHeader header;
    private final ModbusFunction function;

    public ModbusFrame(ModbusHeader header, ModbusFunction function) {
        this.header = header;
        this.function = function;
    }

    public ModbusHeader getHeader() {
        return header;
    }

    public ModbusFunction getFunction() {
        return function;
    }

    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer();

        buf.writeBytes(header.encode());
        buf.writeBytes(function.encode());

        return buf;
    }

    @Override
    public String toString() {
        return "ModbusFrame{" + "header=" + header + ", function=" + function + '}';
    }
}

package com.rytec.rec.channel.ModbusTcpServer.func;

import com.rytec.rec.channel.ModbusTcpServer.entity.ModbusFunction;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by danny on 16-11-22.
 */
public abstract class AbstractFunction extends ModbusFunction {

    protected int address;
    protected int value;

    public AbstractFunction(short functionCode) {
        super(functionCode);
    }

    public AbstractFunction(short functionCode, int address, int quantity) {
        super(functionCode);

        this.address = address;
        this.value = quantity;
    }

    @Override
    public int calculateLength() {
        //function code + address + quantity
        return 1 + 2 + 2;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeShort(address);
        buf.writeShort(value);

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        address = data.readUnsignedShort();
        value = data.readUnsignedShort();
    }
}

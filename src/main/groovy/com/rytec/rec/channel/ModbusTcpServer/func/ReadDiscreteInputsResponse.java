package com.rytec.rec.channel.ModbusTcpServer.func;

import com.rytec.rec.channel.ModbusTcpServer.entity.ModbusFunction;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.BitSet;

/**
 * Created by danny on 16-11-22.
 */
public class ReadDiscreteInputsResponse extends ModbusFunction {

    private short byteCount;
    private BitSet inputStatus;

    public ReadDiscreteInputsResponse() {
        super(READ_DISCRETE_INPUTS);
    }

    public ReadDiscreteInputsResponse(BitSet inputStatus) {
        super(READ_DISCRETE_INPUTS);

        byte[] inputs = inputStatus.toByteArray();

        // maximum of 2000 bits
        if (inputs.length > 250) {
            throw new IllegalArgumentException();
        }

        this.byteCount = (short) inputs.length;
        this.inputStatus = inputStatus;
    }

    public BitSet getInputStatus() {
        return inputStatus;
    }

    public short getByteCount() {
        return byteCount;
    }

    @Override
    public int calculateLength() {
        return 1 + 1 + byteCount;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeByte(byteCount);
        buf.writeBytes(inputStatus.toByteArray());

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        byteCount = data.readUnsignedByte();

        byte[] inputs = new byte[byteCount];
        data.readBytes(inputs);

        inputStatus = BitSet.valueOf(inputs);
    }

    @Override
    public String toString() {
        return "ReadDiscreteInputsResponse{" + "byteCount=" + byteCount + ", coilStatus=" + inputStatus + '}';
    }
}


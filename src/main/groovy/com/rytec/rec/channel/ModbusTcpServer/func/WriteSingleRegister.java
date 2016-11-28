package com.rytec.rec.channel.ModbusTcpServer.func;

/**
 * Created by danny on 16-11-22.
 */
public class WriteSingleRegister extends AbstractFunction {

    //registerAddress;
    //registerValue;
    public WriteSingleRegister() {
        super(WRITE_SINGLE_REGISTER);
    }

    public WriteSingleRegister(int outputAddress, int value) {
        super(WRITE_SINGLE_REGISTER, outputAddress, value);
    }

    public int getRegisterAddress() {
        return address;
    }

    public int getRegisterValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WriteSingleInputRegister{" + "registerAddress=" + address + ", registerValue=" + value + '}';
    }
}

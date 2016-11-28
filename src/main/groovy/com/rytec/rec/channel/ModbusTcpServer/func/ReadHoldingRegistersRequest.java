package com.rytec.rec.channel.ModbusTcpServer.func;

/**
 * Created by danny on 16-11-22.
 */
public class ReadHoldingRegistersRequest extends AbstractFunction {

    //startingAddress = 0x0000 to 0xFFFF
    //quantityOfInputRegisters = 1 - 125
    public ReadHoldingRegistersRequest() {
        super(READ_HOLDING_REGISTERS);
    }

    public ReadHoldingRegistersRequest(int startingAddress, int quantityOfInputRegisters) {
        super(READ_HOLDING_REGISTERS, startingAddress, quantityOfInputRegisters);
    }

    public int getStartingAddress() {
        return address;
    }

    public int getQuantityOfInputRegisters() {
        return value;
    }

    @Override
    public String toString() {
        return "ReadHoldingRegistersRequest{" + "startingAddress=" + address + ", quantityOfInputRegisters=" + value + '}';
    }
}

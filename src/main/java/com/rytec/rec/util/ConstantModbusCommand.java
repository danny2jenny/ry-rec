package com.rytec.rec.util;

/**
 * Created by danny on 17-5-14.
 */
public interface ConstantModbusCommand {
    int READ_COILS = 1;
    int READ_INPUT = 2;
    int READ_HOLDING_REGISTERS = 3;
    int READ_REGISTERS = 4;
    int WRITE_COIL = 5;
    int READ_WRITE_COILS = 999;
}

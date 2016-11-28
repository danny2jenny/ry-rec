package com.rytec.rec.channel.ModbusTcpServer.exception;

import com.rytec.rec.channel.ModbusTcpServer.func.ModbusError;

/**
 * Created by danny on 16-11-22.
 */
public class ErrorResponseException extends Exception {

    int exceptionCode;

    public ErrorResponseException(ModbusError function) {
        super(function.toString());
    }
}


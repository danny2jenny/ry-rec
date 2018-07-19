package com.rytec.rec.channel.Modbus;

import io.netty.util.AttributeKey;

/**
 * Created by danny on 16-12-13.
 * <p>
 * 公共数据
 */
public class ModbusCommon {
    public static final AttributeKey<ModbusTcpSession> MODBUS_STATE = AttributeKey.valueOf("modbus.session");
}

package com.rytec.rec.channel.ModbusTcpServer;

import io.netty.util.AttributeKey;

/**
 * Created by danny on 16-12-13.
 * <p>
 * 公共数据
 */
public class ModbusCommon {
    public static final AttributeKey<ChanneSession> MODBUS_STATE = AttributeKey.valueOf("modbus.session");
}

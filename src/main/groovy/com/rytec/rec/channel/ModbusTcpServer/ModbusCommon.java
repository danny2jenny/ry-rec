package com.rytec.rec.channel.ModbusTcpServer;

import io.netty.util.AttributeKey;

/**
 * Created by danny on 16-12-13.
 * <p>
 * 公共数据
 */
public class ModbusCommon {
    public static final AttributeKey<String> MODBUS_ID = AttributeKey.valueOf("modbus.id");
    public static final AttributeKey<ChannelState> MODBUS_STATE = AttributeKey.valueOf("modbus.session");
}

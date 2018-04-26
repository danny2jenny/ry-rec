package com.rytec.rec.channel.ModbusTcpClient;

import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantFromWhere;
import com.rytec.rec.util.ConstantModbusCommand;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.*;

/**
 * Modbus 客户端传输的消息
 */
public class ModbusClientMsg {
    public int type = ConstantCommandType.GENERAL_READ;         // 消息类型
    public int from = ConstantFromWhere.FROM_USER;              // 消息来源
    public int node = 0;                                        // 节点Id

    public int modbusAddr;                                      // modbus 地址
    public int modbusMsg = ConstantModbusCommand.READ_COILS;    // modbus 命令
    public int modbusOffset = 0;                                // 地址偏移
    public int modbusCount = 1;                                 // 读取数量
    public Object val;                                          // 发送值
    public ModbusRequest readCmdCache;                          // 读命令缓冲

    /**
     * 生成请求对象
     *
     * @return
     */
    public ModbusRequest genReadRequest() throws ModbusTransportException {
        if (readCmdCache != null) {
            return readCmdCache;
        }

        switch (modbusMsg) {
            case ConstantModbusCommand.READ_COILS:
                readCmdCache = new ReadCoilsRequest(modbusAddr, modbusOffset, modbusCount);
                break;
            case ConstantModbusCommand.READ_INPUT:
                readCmdCache = new ReadDiscreteInputsRequest(modbusAddr, modbusOffset, modbusCount);
                break;
            case ConstantModbusCommand.READ_HOLDING_REGISTERS:
                readCmdCache = new ReadHoldingRegistersRequest(modbusAddr, modbusOffset, modbusCount);
                break;
            case ConstantModbusCommand.READ_REGISTERS:
                readCmdCache = new ReadInputRegistersRequest(modbusAddr, modbusOffset, modbusCount);
                break;
            case ConstantModbusCommand.WRITE_COIL:
                readCmdCache = new WriteCoilRequest(modbusAddr, modbusOffset, (Boolean) val);
                break;
            case ConstantModbusCommand.WRITE_REGISTER:
                readCmdCache = new WriteRegisterRequest(modbusAddr, modbusOffset, (Integer) val);
                break;
            case ConstantModbusCommand.READ_WRITE_COILS:
                readCmdCache = new ReadCoilsRequest(modbusAddr, modbusOffset, modbusCount);
                break;
        }
        return readCmdCache;
    }
}

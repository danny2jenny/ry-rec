package com.rytec.rec.node.modbus.cfg;

/**
 * Modbus Node 的配置信息
 */
public class ModbusNodeCfg {
    public int modbusCmd = 1;  // Modbus的命令，1\2\3\4\5\6
    public int regCount = 1;   // 寄存器的数量
    public int regOffset = 0;  // 寄存器偏移量
}

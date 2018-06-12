package com.rytec.rec.node.modbus.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.util.ConstantModbusCommand;

import java.io.IOException;

/**
 * Created by danny on 17-1-21.
 * Node 的基础类，实现配置文件的解析,
 * 以及设定初始值
 */
public abstract class BaseModbusNode extends RecBase implements ModbusNodeInterface {

    /**
     * 各个实现需要设置该值
     */
    public int modbusCmd = ConstantModbusCommand.READ_COILS;  // Modbus的命令，1\2\3\4\5\6
    public int regOffset = 0;  // 寄存器偏移量
    public int interval = 1;

    static public NodeConfig parseConfig(String inStr) {
        NodeConfig nodeConfig;
        try {
            nodeConfig = new ObjectMapper().readValue(inStr, NodeConfig.class);
        } catch (IOException e) {
            nodeConfig = new NodeConfig();

        }

        // 设定初始值
        if (nodeConfig.pA == null) {
            nodeConfig.pA = new Float(1);
        }
        if (nodeConfig.pB == null) {
            nodeConfig.pB = new Float(0);
        }

        if (nodeConfig.sensitive == null) {
            nodeConfig.sensitive = new Float(0);
        }

        return nodeConfig;
    }

    /**
     * 得到命令发送的间隔
     *
     * @return
     */
    @Override
    public int getInterval() {
        return interval;
    }

}

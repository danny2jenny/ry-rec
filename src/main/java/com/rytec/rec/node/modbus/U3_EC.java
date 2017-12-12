package com.rytec.rec.node.modbus;

import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;
import com.rytec.rec.channel.ModbusTcpServer.ModbusMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.util.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 *  * 状态地址：0x0801，(READ_HOLDING_REGISTERS=3)
 * 0：停止
 * 1：工作
 * 控制：WRITE_REGISTER = 6
 */
@Service
@AnnotationNodeType(4001)
@AnnotationJSExport("U3-EC 工业空调")
public class U3_EC extends RS_KTC{

    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_HOLDING_REGISTERS;
        regOffset = 0x0801;
        regCount = 1;   // 寄存器的数量
    }

    /**
     * 需要重载命令生成
     *
     * @param where  从哪里来的真 1 系统 2 联动 3 用户
     * @param nodeId node 的ID
     * @param cmd    命令  对应 util/ConstantCommandType
     * @param value  值
     * @return
     */
    @Override
    public Object genMessage(int where, int nodeId, int cmd, int value) {
        ChannelNode cn = nodeManager.getChannelNodeByNodeId(nodeId).channelNode;
        ModbusMessage frame = new ModbusMessage();

        frame.from = where;
        frame.nodeId = nodeId;
        frame.type = cmd;

        switch (cmd) {
            case ConstantCommandType.GENERAL_READ:
                // 读取状态
                frame.payload = ModbusFrame.readHoldingRegisters(cn.getAdr(), 0x0801, 1);
                frame.responseLen = 7;
                break;
            case ConstantCommandType.GENERAL_WRITE:
                /**
                 * 控制，根据Value来写入不同的寄存器
                 */
                switch (value) {
                    case ConstantAircon.STATE_STOP:
                        // 停止
                        frame.payload = ModbusFrame.writeRegister(cn.getAdr(), 0x0801, 0);
                        break;
                    case ConstantAircon.STATE_COLD:
                        // 制冷
                        frame.payload = ModbusFrame.writeRegister(cn.getAdr(), 0x0801, 1);
                        break;
                    case ConstantAircon.STATE_HOT:
                        // 制热
                        frame.payload = ModbusFrame.writeRegister(cn.getAdr(), 0x0801, 1);
                        break;
                }
                frame.responseLen = 8;
                break;
        }
        return frame;
    }

}

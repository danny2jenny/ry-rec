package com.rytec.rec.node.modbus;

import com.rytec.rec.channel.Modbus.ModbusFrame;
import com.rytec.rec.channel.Modbus.ModbusMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.node.modbus.base.DmaBaseModbus;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 科华环流检测
 * <p>
 * 设备地址 0E
 * 功能码 3
 * 寄存器地址(偏移量) 07D0
 * 字节数8，四个寄存器
 * A、B、C、零序   高位在左，除以10 为最终的结果
 */
@Service
@AnnotationNodeType(1102)
@AnnotationJSExport("科华-环流")
public class KH_Current extends DmaBaseModbus {

    /**
     * 参数初始化
     */
    @PostConstruct
    private void init() {

        // 正式使用
//        modbusCmd = ConstantModbusCommand.READ_HOLDING_REGISTERS;
//        regOffset = 0x07D0;
        // 测试
        modbusCmd = ConstantModbusCommand.READ_REGISTERS;
        regOffset = 0;

    }

    /**
     * 判读数据是否需要更新
     *
     * @param cfg    // Node 的配置对象
     * @param oldVal // 以前记录的值
     * @param newVal // 新值
     * @return
     */
    @Override
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {

        float[] ovl, nvl;

        // 判断是否有数据
        if (newVal == null || oldVal == null) {
            if (newVal != oldVal) {
                return true;
            } else {
                return false;
            }
        }

        ovl = (float[]) oldVal;
        nvl = (float[]) newVal;

        for (int i = 0; i < ovl.length; i++) {
            if (Math.abs(nvl[i] - ovl[i]) >= cfg.sensitive) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void decodeMessage(Object msg) {
        float[] rstFloat = new float[4];        // 返回值
        ModbusMessage modbusMessage = (ModbusMessage) msg;

        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = modbusMessage.from;
        nodeMsg.type = modbusMessage.type;
        nodeMsg.node = modbusMessage.nodeId;
        nodeMsg.value = rstFloat;

        ByteBuf payload = modbusMessage.payload;

        rstFloat[0] = payload.getShort(3) * 0.1F;
        rstFloat[1] = payload.getShort(5) * 0.1F;
        rstFloat[2] = payload.getShort(7) * 0.1F;
        rstFloat[3] = payload.getShort(9) * 0.1F;

        payload.release();

        nodeManager.onMessage(nodeMsg);
    }

    @Override
    public Object genMessage(int where, int nodeId, int cmd, int regCount, int value) {
        ChannelNode cn = nodeManager.getChannelNodeByNodeId(nodeId).channelNode;

        ModbusMessage frame = new ModbusMessage();

        frame.from = where;
        frame.nodeId = nodeId;
        frame.type = cmd;
        frame.regCount = 4;


        switch (modbusCmd) {
            case ConstantModbusCommand.READ_HOLDING_REGISTERS:      // 3
                frame.payload = ModbusFrame.readHoldingRegisters(cn.getAdr(), regOffset, 4);
                frame.responseLen = 5 + 4 * 2;
                break;
            case ConstantModbusCommand.READ_REGISTERS:              // 4
                frame.payload = ModbusFrame.readRegisters(cn.getAdr(), regOffset, 4);
                frame.responseLen = 5 + 4 * 2;
                break;
        }
        return frame;
    }

}

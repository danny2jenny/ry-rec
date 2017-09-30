package com.rytec.rec.node.modbus;


import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 有温湿度的功能，直接使用 RS_WSD
 * 注意，用户设置的时候，以下端口需要加1
 * 185：制冷
 * 186：制热
 * 187：关机
 */
@Service
@AnnotationNodeType(2004)
@AnnotationJSExport("RS-KTC 空调")
public class RS_KTC extends NodeModbusBase{
    @Override
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return false;
    }

    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_HOLDING_REGISTERS;
        regOffset = 185;
        regCount = 3;   // 寄存器的数量
    }
}

package com.rytec.rec.node.modbus;

import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.ValueCompare;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@AnnotationNodeType(1006)
@AnnotationJSExport("DMA 输出 - 2")
public class DAMOutput_2 extends NodeModbusBase {
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return ValueCompare.booleanNeedUpdate(cfg, oldVal, newVal);
    }

    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_WRITE_COILS;
        regCount = 2;   // 寄存器的数量
    }
}

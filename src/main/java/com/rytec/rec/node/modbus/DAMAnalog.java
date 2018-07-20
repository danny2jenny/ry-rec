package com.rytec.rec.node.modbus;

import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.ValueCompare;
import com.rytec.rec.node.modbus.base.DmaBaseModbus;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by danny on 17-6-5.
 */

@Service
@AnnotationNodeType(1004)
@AnnotationJSExport("DMA 模拟量")
public class DAMAnalog extends DmaBaseModbus {
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return ValueCompare.analogNeedUpdate(cfg, oldVal, newVal);
    }

    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_REGISTERS;
    }
}

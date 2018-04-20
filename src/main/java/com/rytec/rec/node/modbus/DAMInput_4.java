package com.rytec.rec.node.modbus;

import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.ValueCompare;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@AnnotationNodeType(1005)
@AnnotationJSExport("DMA 输入 -4")
public class DAMInput_4 extends NodeModbusBase{
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return ValueCompare.booleanNeedUpdate(cfg, oldVal, newVal);
    }

    @PostConstruct
    private void init(){
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_INPUT;
        regCount = 4;   // 寄存器的数量
    }
}

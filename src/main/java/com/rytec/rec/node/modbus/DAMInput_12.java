package com.rytec.rec.node.modbus;

import com.rytec.rec.node.*;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by danny on 16-12-14.
 * <p>
 * 命令格式：
 * byte     modbus地址
 * byes     0x02 命令
 * word     端口地址，从0开始
 * word     数量：1
 * word     CRC16 (不生成)
 * <p>
 * 返回格式：
 * byte     modbus地址
 * byes     0x02 命令
 * byte     字节数
 * byte     端口值
 * word     CRC16 (不生成)
 */

@Service
@AnnotationNodeType(1002)
@AnnotationJSExport("DMA 输入 -12")
public class DAMInput_12 extends NodeModbusBase {

    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return ValueCompare.booleanNeedUpdate(cfg, oldVal, newVal);
    }

    @PostConstruct
    private void init(){
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_INPUT;
        regCount = 12;   // 寄存器的数量
    }


}



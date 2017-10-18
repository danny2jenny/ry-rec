package com.rytec.rec.node.modbus;

import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.ValueCompare;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


/**
 * Created by danny on 17-2-8.
 * <p>
 * 设置485地址
 * 广播地址  功能号   地址     新地址   CRC
 * 00       06      2000    0001    421b    发送
 * 01       06      2000    0001    43ca    返回
 * <p>
 * 数据读取
 * 地址   功能号     寄存器     读取数量    CRC
 * 02     03        002a      0001      a5f1
 * <p>
 * 返回数据
 * 地址   功能号     字节数        值      CRC
 * 02     03        02        0000     fc44
 *
 */
@Service
@AnnotationNodeType(3001)
@AnnotationJSExport("HT-C1 温湿度")
public class HT_C1 extends NodeModbusBase {
    @Override
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return ValueCompare.analogNeedUpdate(cfg, oldVal, newVal);
    }

    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_HOLDING_REGISTERS;
        regCount = 1;                   // 寄存器的数量
        regOffset = 0x2A;               // 寄存器偏移
    }
}

package com.rytec.rec.node.modbus;

import com.rytec.rec.node.*;
import com.rytec.rec.node.modbus.base.DmaModbusBase;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by danny on 16-12-13.
 * <p>
 * 北京聚英电子相关的Modbus控制器
 * <p>
 * <p>
 * 写命令格式：
 * byte     modbus地址
 * byes     0x05 命令
 * word     端口地址，从0开始
 * word     开：FF 00, 关 00 00
 * word     CRC16 (不生成)
 * <p>
 * 返回格式：
 * byte     modbus地址
 * byes     0x05 命令
 * word     端口地址，从0开始
 * word     开：FF 00, 关 00 00
 * word     CRC16 (不生成)
 * <p>
 * -----------------------------------
 * 读命令：
 * byte     modbus地址
 * byes     0x01 命令
 * word     端口地址，从0开始
 * word     数量 1
 * word     CRC16 (不生成)
 * <p>
 * 返回格式：
 * byte     modbus地址
 * byes     0x01 命令
 * byte     字节数 1
 * byte     开：FF , 关 00
 * word     CRC16 (不生成)
 */

@Service
@AnnotationNodeType(1001)
@AnnotationJSExport("DMA 输出")
public class DAMOutput extends DmaModbusBase {

    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return ValueCompare.booleanNeedUpdate(cfg, oldVal, newVal);
    }

    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_WRITE_COILS;
    }

}

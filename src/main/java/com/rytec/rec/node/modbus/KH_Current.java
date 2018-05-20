package com.rytec.rec.node.modbus;

import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantModbusCommand;
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
public class KH_Current extends NodeModbusBase {

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

    /**
     * 参数初始化
     */
    @PostConstruct
    private void init() {
        /**
         * 各个实现需要设置该值
         */
        modbusCmd = ConstantModbusCommand.READ_HOLDING_REGISTERS;
        //modbusCmd = ConstantModbusCommand.READ_REGISTERS;
        regCount = 4;   // 寄存器的数量
        //regOffset = 0;
        regOffset = 0x07D0;
    }


}

package com.rytec.rec.node.Fiber;

import com.rytec.rec.app.RecBase;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.modbus.base.ModbusNodeInterface;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import org.springframework.stereotype.Service;

@Service
@AnnotationNodeType(1103)
@AnnotationJSExport("科华-光纤测温")
public class FiberAlarm extends RecBase implements ModbusNodeInterface {
    @Override
    public Object genMessage(int where, int nodeId, int cmd, int regCount, int value) {
        return null;
    }

    @Override
    public void decodeMessage(Object msg) {

    }

    @Override
    public int sendMessage(NodeMessage nodeMsg) {
        return 0;
    }

    @Override
    public boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        return true;
    }

    @Override
    public void goodHelth(Object msg, boolean h) {

    }

    @Override
    public Object getCfg() {
        return null;
    }

    @Override
    public int getInterval() {
        return 0;
    }


}

package com.rytec.rec.node.node;

import com.rytec.rec.node.BaseNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeMessage;

/**
 * Created by danny on 17-1-21.
 * 开关量输入设备
 */
public abstract class NodeInput extends BaseNode implements NodeInterface {

    /**
     * @param cfg
     * @param oldVal
     * @param newVal
     * @return
     */
    public boolean valueCompare(NodeConfig cfg, Object oldVal, Object newVal) {
        boolean rst = false;
        if (oldVal != newVal) {
            rst = true;
        }
        return rst;
    }

    public int sendMessage(NodeMessage nodeMsg) {
        return 0;
    }
}

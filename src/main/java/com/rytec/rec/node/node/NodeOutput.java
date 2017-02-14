package com.rytec.rec.node.node;

import com.rytec.rec.node.BaseNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeInterface;

/**
 * Created by danny on 17-1-21.
 * <p>
 * 开关节点对数据进行过滤
 */
public abstract class NodeOutput extends BaseNode implements NodeInterface {

    /**
     *
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
}

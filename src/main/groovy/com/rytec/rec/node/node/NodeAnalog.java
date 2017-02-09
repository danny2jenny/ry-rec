package com.rytec.rec.node.node;

import com.rytec.rec.node.BaseNode;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeMessage;

/**
 * Created by danny on 17-1-21.
 */
public abstract class NodeAnalog extends BaseNode implements NodeInterface {
    /**
     * @param cfg    node 的配置，主要是对数据进行处理。例如返回的整形转换成浮点类型
     * @param oldVal 原始值
     * @param newVal 新值
     * @return 如果超过Sensitiv的绝对值，返回true，需要更新；否者false，不需要更新
     */
    public boolean valueCompare(NodeConfig cfg, Object oldVal, Object newVal) {
        boolean rst = false;

        if (oldVal == null) {
            rst = true;
        } else {
            if (Math.abs((Float) newVal - (Float) oldVal) >= cfg.sensitive) {
                rst = true;
            }
        }
        return rst;

    }

    // 没有主动发送命令
    public int sendMessage(NodeMessage msg) {
        return 0;
    }
}

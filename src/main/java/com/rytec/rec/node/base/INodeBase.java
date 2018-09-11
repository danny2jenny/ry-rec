package com.rytec.rec.node.base;

import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeMessage;

/**
 * 最基本的Node接口，适合大多的Node
 */
public interface INodeBase extends INode {

    // 健康状态
    void goodHelth(Object msg, boolean h);

    // 是否需要更新
    boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal);

    // 发送消息
    int sendMessage(NodeMessage nodeMsg);
}

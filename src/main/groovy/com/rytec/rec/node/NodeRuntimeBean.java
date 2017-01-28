package com.rytec.rec.node;

import com.rytec.rec.db.model.ChannelNode;

/**
 * Created by danny on 17-1-15.
 * <p>
 * 用于管理ChannelNode的Bean
 * 每个Bean对应一个ChannelNode和一个用于状态的Bean
 */
public class NodeRuntimeBean {
    public ChannelNode channelNode;
    public NodeState nodeState;
    public NodeConfig nodeConfig;
}

package com.rytec.rec.node;

import com.rytec.rec.db.model.ChannelNode;

/**
 * Created by danny on 17-1-15.
 * <p>
 * 用于管理ChannelNode的Bean
 * 每个Bean对应一个ChannelNode和一个用于状态的Bean
 */
public class NodeRuntimeBean {

    private NodeInterface nodeInterface;        //

    public ChannelNode channelNode;         // Node 和 Channel 的对应关系
    public NodeState nodeState;             // Node 的当前状态，值
    public NodeConfig nodeConfig;           // Node 配置文件
    public int helth = 10;                  // 健康度


    public NodeRuntimeBean(NodeInterface nodeMgr) {
        this.nodeInterface = nodeMgr;
    }

    /**
     * 良好的状态计数
     */
    public void goodHelth(Object msg, Boolean h) {
        if (h) {
            helth = 10;
            nodeInterface.goodHelth(msg, true);              // 健康
        } else {
            helth--;
            if (helth < 0) {
                helth = 0;
                nodeInterface.goodHelth(msg, false);         // 不健康
            }
        }
    }


}

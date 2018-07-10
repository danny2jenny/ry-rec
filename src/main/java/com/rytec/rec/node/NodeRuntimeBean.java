package com.rytec.rec.node;

import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.modbus.base.ModbusNodeInterface;

/**
 * Created by danny on 17-1-15.
 * <p>
 * 用于管理ChannelNode的Bean
 * 每个Bean对应一个ChannelNode和一个用于状态的Bean
 */
public class NodeRuntimeBean {

    private ModbusNodeInterface modbusNodeInterface;

    public ChannelNode channelNode;         // Node 和 Channel 的对应关系
    public NodeState nodeState;             // Node 的当前状态，值
    public NodeConfig nodeConfig;           // Node 配置文件
    public int helth = 10;                  // 健康度


    public NodeRuntimeBean(ModbusNodeInterface nodeMgr) {
        this.modbusNodeInterface = nodeMgr;
    }

    /**
     * 良好的状态计数
     */
    public void goodHelth(Object msg, Boolean h) {
        if (h) {
            helth = 10;
            modbusNodeInterface.goodHelth(msg, true);              // 健康
        } else {
            helth--;
            if (helth < 0) {
                helth = 0;
                modbusNodeInterface.goodHelth(msg, false);         // 不健康
            }
        }
    }


}

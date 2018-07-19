package com.rytec.rec.channel.Modbus;

import com.rytec.rec.app.RecBase;
import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.modbus.base.ModbusNodeInterface;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * Modbus Client 和 Server 的基类
 */
public abstract class ChannelModbusBase extends RecBase implements IChannelModbus {

    @Autowired
    DbConfig dbConfig;

    @Autowired
    public NodeManager nodeManager;

    @Autowired
    ChannelManager channelManager;

    /*
     * 两级 HashMap
     * 第一级：ip:port->Map
     * 第二级：nodeId->ChannelNode
     */
    HashMap<Object, HashMap> channelNodes = new HashMap();


    /**
     * 通讯层接收到数据
     *
     * @param key
     * @param response
     */
    public void receiveMsg(Object key, ModbusMessage response) {
        ChannelNode cn = (ChannelNode) channelNodes.get(key).get(response.nodeId);
        ModbusNodeInterface nodeBean = nodeManager.getNodeComInterface(cn.getNtype());

        // 解码值
        nodeBean.decodeMessage(response);
    }

    public void channelOnline(Object channelId, boolean online) {
        HashMap<Integer, ChannelNode> channelNodeMap = channelNodes.get(channelId);
        if (channelNodeMap == null) {
            return;
        }

        if (channelNodeMap.values().size() > 0) {
            ChannelNode cn = (ChannelNode) channelNodeMap.values().toArray()[0];

            if (online) {
            } else {
                channelManager.channelOffline(cn.getId());
            }

        }
    }

}

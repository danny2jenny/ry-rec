package com.rytec.rec.channel.Modbus;

import com.rytec.rec.app.RecBase;
import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.node.NodeManager;
import org.springframework.beans.factory.annotation.Autowired;

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


    /**
     * 通讯层接收到数据
     *
     * @param key
     * @param response
     */
    public void receiveMsg(Object key, ModbusMessage response) {

    }


    public int sendMsg(Object msg) {
        return 0;
    }

    public void channelOnline(Object channelId, boolean online) {

    }


}

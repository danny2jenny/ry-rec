package com.rytec.rec.channel.Modbus;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.db.model.ChannelNode;

import java.util.List;

/**
 * Modbus 通道管理的接口
 */
public interface IChannelModbus extends ChannelInterface {

    /**
     * 添加连接
     *
     * @param key
     * @param client
     */
    void addClient(Object key, Object client);

    /**
     * 删除连接
     *
     * @param key
     */
    void delClient(Object key);

    /**
     * 通过Key值返回ChannelNode列表，
     * Key一般是通道的标识
     *
     * @param key
     * @return
     */
    List<ChannelNode> getChannelNodes(Object key);

    /**
     * 收到一个Node的消息
     * key 一般是通道标识
     *
     * @param key
     * @param response
     */
    void receiveMsg(Object key, ModbusMessage response);

}

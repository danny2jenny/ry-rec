package com.rytec.rec.channel.ModbusTcpServer;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by danny on 16-12-14.
 * 每个TCP连接对应Channel的状态
 * 1、包含一个查询用的List
 * 2、每次从List中提取一个命令发送，查询远端状态
 * 3、当命令队列中有数据的时候，优先满足队列中的命令
 */
public class ChanneSession {

    public String id;               //channel的id  ip：port

    public ModbusFrame lastCmd;     //最后一次发送的命令

    public List<ModbusFrame> queryCmd = new ArrayList();

    //发送队列
    BlockingQueue<ModbusFrame> sendQueue = new LinkedBlockingQueue(10);

    // 用于状态查询的命令帧，该
    public HashMap<String, ModbusFrame> stateCmd = new HashMap();

    public ChanneSession(String cid) {
        id = cid;
    }

    //得到下一个的查询命令
    private int currentQuerryIndex = 0;

    private ModbusFrame getNextQuery() {
        ModbusFrame msg = queryCmd.get(currentQuerryIndex);
        currentQuerryIndex = (currentQuerryIndex + 1) % queryCmd.size();
        return msg;
    }

    public void putCmd(ModbusFrame msg) {
        sendQueue.add(msg);
    }

    /*
    * 处理命令队列
    * 优先处理命令队列，然后再处理查询命令
    */
    public void processQueue(Channel cha) {

    }

}

package com.rytec.rec.channel.ModbusTcpServer;

import io.netty.channel.Channel;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by danny on 16-12-14.
 * 每个TCP连接对应Channel的状态
 * 1、包含一个查询用的List
 * 2、每次从List中提取一个命令发送，查询远端状态
 * 3、当命令队列中有数据的时候，优先满足队列中的命令
 */
public class ChanneSession {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private Channel cha;            //对应的Channel
    private int currentQuerryIndex = 0;

    public String id;               //channel的id  ip：port

    public volatile ModbusMessage lastCmd = null;     //最后一次发送的命令

    public List<ModbusMessage> queryCmd = new ArrayList();

    //发送队列
    volatile Queue<ModbusMessage> sendQueue = new LinkedList();

    // 用于状态查询的命令帧，该
    public HashMap<String, ModbusMessage> stateCmd = new HashMap();

    public ChanneSession(String cid, Channel channel) {
        id = cid;
        cha = channel;
    }

    //当前通道是否在等待回应
    private boolean isBusy() {
        return lastCmd == null ? true : false;
    }


    //得到下一个的查询命令
    private ModbusMessage getNextQuery() {
        if (queryCmd.size() == 0) {
            return null;
        }
        ModbusMessage msg = queryCmd.get(currentQuerryIndex);
        currentQuerryIndex = (currentQuerryIndex + 1) % queryCmd.size();
        return msg;
    }


    public void sendMsg(ModbusMessage msg) {
        sendQueue.add(msg);
        processQueue();
    }

    /*
    * 处理命令队列
    * 优先处理命令队列，然后再处理查询命令
    */
    public void processQueue() {
        if (lastCmd != null) return;

        if (sendQueue.size() > 0) {
            lastCmd = sendQueue.poll();
        } else {
            lastCmd = getNextQuery();
        }

        if (lastCmd != null) {
            cha.writeAndFlush(lastCmd);
        }

    }

}

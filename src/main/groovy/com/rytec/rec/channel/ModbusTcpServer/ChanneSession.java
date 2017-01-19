package com.rytec.rec.channel.ModbusTcpServer;

import com.rytec.rec.channel.ChannelMessage;
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

    //对应的Channel
    private Channel cha;

    // 当前定时队列的位置
    private int currentTimerQuerryIndex = 0;

    //channel的id  ip：port
    public String id;

    // 最后一次发送的命令
    public volatile ChannelMessage lastCmd = null;

    // 需要定时发送的队列命令
    public List<ChannelMessage> timerQueryCmd = new ArrayList();

    // 非定时队列的发送命令(用户，或者是联动)
    volatile Queue<ChannelMessage> instantQueueCmd = new LinkedList();

    /**
     * @param cid
     * @param channel
     */
    public ChanneSession(String cid, Channel channel) {
        id = cid;
        cha = channel;
    }

    /**
     * 当前通道是否在等待回应
     */
    private boolean isBusy() {
        return lastCmd == null ? true : false;
    }


    /**
     * 得到下一个的查询命令
     */

    private ChannelMessage getNextQuery() {
        if (timerQueryCmd.size() == 0) {
            return null;
        }
        ChannelMessage msg = timerQueryCmd.get(currentTimerQuerryIndex);
        currentTimerQuerryIndex = (currentTimerQuerryIndex + 1) % timerQueryCmd.size();
        return msg;
    }


    /**
     * 发送命令
     *
     * @param msg
     */
    public void sendMsg(ChannelMessage msg) {
        instantQueueCmd.add(msg);
        processQueue();
    }

    /*
    * 处理命令队列
    * 优先处理命令队列，然后再处理查询命令
    */
    public void processQueue() {
        if (lastCmd != null) return;

        if (instantQueueCmd.size() > 0) {
            lastCmd = instantQueueCmd.poll();
        } else {
            lastCmd = getNextQuery();
        }

        if (lastCmd != null) {
            cha.writeAndFlush(lastCmd);
        }

    }

}

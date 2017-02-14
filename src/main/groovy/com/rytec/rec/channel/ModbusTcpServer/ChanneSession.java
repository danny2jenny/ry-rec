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

    private ModbusTcpServer modbusTcpServer;

    //对应的Channel
    private Channel cha;

    // 当前定时队列的位置
    private int currentTimerQuerryIndex = 0;

    //channel的id  ip：port
    public String id;

    // 定时器，
    private volatile int timmer = 0;

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
    public ChanneSession(ModbusTcpServer mts, String cid, Channel channel) {
        id = cid;
        cha = channel;
        modbusTcpServer = mts;
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
        timerProcess();
    }

    /*
    * 处理命令队列
    * 优先处理命令队列，然后再处理查询命令
    */
    public void timerProcess() {
        //logger.debug("Timmer:" + timmer);
        // 如果当前有未完成的命令，返回
        if (lastCmd != null) {
            // 判断超时
            timmer++;
            if (timmer > 10) {
                modbusTcpServer.nodeError(lastCmd);

                lastCmd = null;
            } else {
                return;
            }
        }

        // 首先满足实时队列
        if (instantQueueCmd.size() > 0) {
            //实时队列
            lastCmd = instantQueueCmd.poll();
        } else {
            //定时发送
            lastCmd = getNextQuery();
        }

        // 如果存在当前命令，就发送
        if (lastCmd != null) {
            timmer = 0;
            cha.writeAndFlush(lastCmd);
        }

    }

}

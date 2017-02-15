package com.rytec.rec.channel.ModbusTcpServer;

import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.channel.Channel;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    //channel的id  ip：port
    public String id;

    // ***********************以下变量需要同步**************************
    // 读取不需要锁定，写需要锁定
    // 最后一次发送的命令
    public volatile ChannelMessage lastCmd = null;
    // 当前定时队列的位置
    private volatile Integer currentTimerQuerryIndex = 0;

    // 以下变量不需要锁定

    // 非定时队列的发送命令(用户，或者是联动)
    // 可能多线程访问
    private Queue<ChannelMessage> instantQueueCmd = new ConcurrentLinkedQueue();
    // 需要定时发送的队列命令
    public volatile List<ChannelMessage> timerQueryCmd = new ArrayList();

    // ***************************************************************


    /**
     * @param cid
     * @param channel
     */
    public ChanneSession(ModbusTcpServer mts, String cid, Channel channel) {
        id = cid;
        cha = channel;
        modbusTcpServer = mts;

        //设置查询命令集合
        HashMap<Integer, ChannelNode> cha = modbusTcpServer.channelNodes.get(cid);

        for (ChannelNode cn : cha.values()) {

            NodeInterface node = modbusTcpServer.nodeManager.getNodeComInterface(cn.getNtype());

            if (node!=null){
                ChannelMessage msg = node.genMessage(ConstantFromWhere.FROM_TIMER, cn.getNid(), ConstantCommandType.GENERAL_READ, 0);
                timerQueryCmd.add(msg);
            }

        }
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
    * todo: 这里是定时线程，需要同步
    */
    public synchronized void timerProcess() {

        // 如果有未返回的命令，退出
        if (lastCmd != null) {
            return;
        }

        // 首先满足实时队列
        lastCmd = instantQueueCmd.poll();

        if (lastCmd == null) {
            lastCmd = getNextQuery();
        }

        // 如果存在当前命令，就发送
        if (lastCmd != null) {
            cha.writeAndFlush(lastCmd);
        }
    }

}

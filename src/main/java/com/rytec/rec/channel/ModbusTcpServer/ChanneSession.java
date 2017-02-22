package com.rytec.rec.channel.ModbusTcpServer;

import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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
    private Channel channel;

    //channel的id  ip：port
    public String id;

    // ***********************以下变量需要同步**************************
    // 读取不需要锁定，写需要锁定
    // 最后一次发送的命令
    private volatile ChannelMessage lastOutMsg = null;
    // 当前定时队列的位置
    private volatile int currentTimerQuerryIndex = 0;
    // 当前超时的计数
    private volatile int timer = 0;


    // 以下变量不需要锁定

    // 非定时队列的发送命令(用户，或者是联动)
    // 可能多线程访问
    private Queue<ChannelMessage> instantQueueCmd = new ConcurrentLinkedQueue();
    // 需要定时发送的队列命令
    private volatile List<ChannelMessage> timerQueryList = new ArrayList();

    public ChannelMessage getLastOutMsg() {
        return lastOutMsg;
    }

    /**
     * 清除当前发送的消息
     */
    public synchronized void clearLastOutMsg() {
        timer = 0;
        lastOutMsg = null;
    }

    // ***************************************************************

    /**
     * @param cid
     * @param channel
     */
    public ChanneSession(ModbusTcpServer mts, String cid, Channel channel) {
        id = cid;
        this.channel = channel;
        modbusTcpServer = mts;

        //设置查询命令集合
        HashMap<Integer, ChannelNode> cha = modbusTcpServer.channelNodes.get(cid);

        for (ChannelNode cn : cha.values()) {

            NodeInterface node = modbusTcpServer.nodeManager.getNodeComInterface(cn.getNtype());

            if (node != null) {
                ChannelMessage msg = node.genMessage(ConstantFromWhere.FROM_TIMER, cn.getNid(), ConstantCommandType.GENERAL_READ, 0);
                timerQueryList.add(msg);
            }

        }
    }


    /**
     * 得到下一个的查询命令
     */

    private ChannelMessage getNextQuery() {
        if (timerQueryList.size() == 0) {
            return null;
        }
        ChannelMessage msg = timerQueryList.get(currentTimerQuerryIndex);
        currentTimerQuerryIndex = (currentTimerQuerryIndex + 1) % timerQueryList.size();
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

    /**
     * 检查超时
     */
    public void checkOverTime() {
        timer = 0;
        logger.debug("超时：" + lastOutMsg.nodeId);
        lastOutMsg = null;
        //todo: 超时处理
    }

    /**
     * 处理命令队列
     * 优先处理命令队列，然后再处理查询命令
     */
    public synchronized void timerProcess() {

        // 如果有未返回的命令，检查超时
        if (lastOutMsg != null) {
            timer++;
            if (timer > 4) {
                //超时处理
                checkOverTime();
            } else {
                //没有超时
                return;
            }
        }

        // 首先满足实时队列
        lastOutMsg = instantQueueCmd.poll();

        if (lastOutMsg == null) {
            lastOutMsg = getNextQuery();
        }

        // 如果存在当前命令，就发送
        if (lastOutMsg != null) {
            channel.writeAndFlush(lastOutMsg);
        }
    }

    /**
     * 单独处理队列，由Channel收到回应后调用
     */
    public synchronized void processQueue() {
        lastOutMsg = instantQueueCmd.poll();

        if (lastOutMsg == null) {
            lastOutMsg = getNextQuery();
        }

        // 如果存在当前命令，就发送
        if (lastOutMsg != null) {
            channel.writeAndFlush(lastOutMsg);
        }
    }

}

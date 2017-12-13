package com.rytec.rec.channel.ModbusTcpServer;

import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeRuntimeBean;
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
public class ModbusChannelSession {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private ModbusTcpServer modbusTcpServer;

    //对应的Channel
    private Channel channel;

    //channel的id  ip：port
    public String id;

    // ***********************以下变量需要同步**************************
    // 读取不需要锁定，写需要锁定
    // 最后一次发送的命令
    private volatile ModbusMessage lastOutMsg = null;
    // 当前定时队列的位置
    private volatile int currentTimerQuerryIndex = 0;
    // 当前超时的计数
    private volatile int timer = 0;


    // 以下变量不需要锁定

    // 非定时队列的发送命令(用户，或者是联动)
    // 可能多线程访问
    private Queue<ModbusMessage> instantQueueCmd = new ConcurrentLinkedQueue();

    // 需要定时发送的队列命令
    // "nodeType:adr" -> ModbusMessage
    private volatile HashMap<String, ModbusMessage> timerQueryList = new HashMap();
    private volatile List<String> timerQueryListIndex = new ArrayList();

    public ModbusMessage getLastOutMsg() {
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
    public ModbusChannelSession(ModbusTcpServer mts, String cid, Channel channel) {
        id = cid;
        this.channel = channel;
        modbusTcpServer = mts;

        //设置查询命令集合
        HashMap<Integer, ChannelNode> cha = modbusTcpServer.channelNodes.get(cid);

        /*
         * 地址一样的，组织成一个命令进行查询
         * 可能该通道没有被配置 cha 可能为空
         */

        if (cha == null) {
            return;
        }
        for (ChannelNode cn : cha.values()) {

            // 得到 node 对应的操作接口
            NodeInterface iNode = modbusTcpServer.nodeManager.getNodeComInterface(cn.getNtype());

            /**
             * Node类型一致，地址一致，组成一个查询
             */
            if (iNode != null) {
                String key = "" + cn.getNtype() + ':' + cn.getAdr();
                if (timerQueryList.get(key) == null) {
                    timerQueryListIndex.add(key);
                    ModbusMessage msg = (ModbusMessage) iNode.genMessage(ConstantFromWhere.FROM_TIMER, cn.getNid(), ConstantCommandType.GENERAL_READ, 0);
                    timerQueryList.put(key, msg);
                }
            }
        }
    }


    /**
     * 得到下一个的查询命令
     */

    private ModbusMessage getNextQuery() {
        if (timerQueryList.size() == 0) {
            return null;
        }
        ModbusMessage msg = (ModbusMessage) timerQueryList.get(timerQueryListIndex.get(currentTimerQuerryIndex));
        currentTimerQuerryIndex = (currentTimerQuerryIndex + 1) % timerQueryList.size();
        return msg;
    }


    /**
     * 发送命令
     *
     * @param msg
     */
    public void sendMsg(ModbusMessage msg) {
        instantQueueCmd.add(msg);
        timerProcess();
    }

    /**
     * 检查超时
     */
    public void checkOverTime() {
        logger.debug("超时--Node：" + lastOutMsg.nodeId);
        timer = 0;
        //todo: 超时处理
        goodHelth(lastOutMsg,false);

        /**
         * 如果是用户命令，或者是联动命令，需要重新执行
         * 重新执行超过三次还不成功就丢弃
         */
        //
        if ((lastOutMsg.from == ConstantFromWhere.FROM_USER)||(lastOutMsg.from == ConstantFromWhere.FROM_ALI)){
            lastOutMsg.retry++;
            if (lastOutMsg.retry<3){
                sendMsg(lastOutMsg);
                logger.debug("命令重试：Node:"+lastOutMsg.nodeId);
            } else {
                logger.debug("重试失败！！！！：Node:"+lastOutMsg.nodeId);
                lastOutMsg = null;
            }
        } else {
            lastOutMsg = null;
        }

    }

    /**
     * 处理命令队列
     * 优先处理命令队列，然后再处理查询命令
     */
    public synchronized void timerProcess() {
        // todo 这种方式判断有问题！！！！
        // 如果有未返回的命令，检查超时
        if (lastOutMsg != null) {
            timer++;
            if (timer > 5) {
                //超时处理
                checkOverTime();
            } else {
                //没有超时
                return;
            }
        }

        // 首先满足实时队列
        lastOutMsg = instantQueueCmd.poll();

        // 没有实时命令，发送定时命令
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

    /**
     * 更新健康度
     *
     * @param h
     */
    public void goodHelth(ModbusMessage msg, Boolean h) {
        NodeRuntimeBean nodeRuntimeBean = modbusTcpServer.nodeManager.getChannelNodeByNodeId(msg.nodeId);
        nodeRuntimeBean.goodHelth(msg, h);
    }

}

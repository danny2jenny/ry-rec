package com.rytec.rec.channel.Modbus.common;

import com.rytec.rec.channel.Modbus.ChannelModbusBase;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.modbus.base.IModbusNode;
import com.rytec.rec.node.NodeRuntimeBean;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantFromWhere;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by danny on 16-12-14.
 * 每个TCP连接对应Channel的状态
 * 1、包含一个查询用的List
 * 2、每次从List中提取一个命令发送，查询远端状态
 * 3、当命令队列中有数据的时候，优先满足队列中的命令
 * <p>
 * 这个Session对象可以适合Client和Server
 */
public class ModbusTcpSession {

    private ChannelModbusBase channelModbus;
    //对应的Channel
    private Channel channel;
    //channel的id  ip：port
    public Object id;


    // ***********************以下变量需要同步**************************
    // 读取不需要锁定，写需要锁定
    // 最后一次发送的命令
    private volatile ModbusMessage lastOutMsg = null;
    // 当前定时队列的位置
    private volatile int currentTimerQuerryIndex = 0;
    // 当前超时的计数
    private volatile int timer = 0;
    // 命令计数
    private volatile int checkCount = 0;        // 定时检测的计数
    private volatile int sendCount = 0;         // 命令发送的计数，用来确定当前发送那条定时命令


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
    public ModbusTcpSession(ChannelModbusBase mts, Object cid, Channel channel) {
        this.id = cid;
        this.channel = channel;
        channelModbus = mts;

        //设置查询命令集合
        List<ChannelNode> chas = channelModbus.getChannelNodes(cid);

        /*
         * 地址一样的，组织成一个命令进行查询
         * 可能该通道没有被配置 cha 可能为空
         */

        if (chas == null) {
            return;
        }
        for (ChannelNode cn : chas) {

            // 得到 node 对应的操作接口
            IModbusNode iNode = channelModbus.nodeManager.getNodeComInterface(cn.getNtype());

            if (iNode == null) {
                continue;
            }

            /**
             * Node类型一致，地址一致，组成一个查询
             * 根据Node的no字段来动态生成查询命令
             */
            NodeRuntimeBean nodeRuntimeBean = channelModbus.nodeManager.getChannelNodeByNodeId(cn.getNid());
            String key = "" + cn.getNtype() + ':' + cn.getAdr();
            ModbusMessage msg = timerQueryList.get(key);
            if (msg == null) {
                // 不存在命令，建立一个新的命令
                msg = (ModbusMessage) iNode.genMessage(ConstantFromWhere.FROM_TIMER, cn.getNid(), ConstantCommandType.GENERAL_READ, cn.getNo(), 0);
                if (msg != null) {
                    timerQueryListIndex.add(key);
                    // 初始化命令发送的周期
                    msg.interval = nodeRuntimeBean.nodeConfig.interval;
                    msg.intervalCount = 1;
                }
            } else {
                // 存在命令，根据 regCount 更新当前的命令
                if (cn.getNo() > msg.regCount) {
                    msg = (ModbusMessage) iNode.genMessage(ConstantFromWhere.FROM_TIMER, cn.getNid(), ConstantCommandType.GENERAL_READ, cn.getNo(), 0);
                    // 初始化命令发送的周期
                    msg.interval = nodeRuntimeBean.nodeConfig.interval;
                    msg.intervalCount = 1;
                }
            }

            if (msg != null) {
                timerQueryList.put(key, msg);
            }

        }
    }


    /**
     * 得到下一个的查询命令
     * 根据命令周期得到
     */

    private ModbusMessage getNextQuery() {
        if (timerQueryList.size() == 0) {
            return null;
        }
        ModbusMessage msg = timerQueryList.get(timerQueryListIndex.get(currentTimerQuerryIndex));
        currentTimerQuerryIndex = (currentTimerQuerryIndex + 1) % timerQueryList.size();

        // 判断该命令是否需要发送
        msg.intervalCount--;
        if (msg.intervalCount == 0) {
            msg.intervalCount = msg.interval;
            return msg;
        } else {
            return null;
        }

    }


    /**
     * 发送命令，只是把命令放入队列
     *
     * @param msg
     */
    public void sendMsg(ModbusMessage msg) {
        instantQueueCmd.add(msg);
    }

    /**
     * 处理命令队列
     * 优先处理命令队列，然后再处理查询命令
     */
    public synchronized void timerProcess() {
        // 如果有未返回的命令，检查超时
        if ((lastOutMsg != null) && (sendCount == checkCount)) {

            // 命令没有变化
            timer++;

            if (timer < lastOutMsg.overtime) {
                // 未到达超时计数，返回
                return;
            }

            channelModbus.debug("超时--Node：" + lastOutMsg.nodeId);
            timer = 0;

            goodHelth(lastOutMsg, false);

            /**
             * 如果是用户命令，或者是联动命令，需要重新执行
             * 重新执行超过三次还不成功就丢弃
             */
            if ((lastOutMsg.from == ConstantFromWhere.FROM_SYSTEM) || (lastOutMsg.from == ConstantFromWhere.FROM_ALI)) {
                lastOutMsg.retry++;
                if (lastOutMsg.retry < 3) {
                    // 重新发送当前的命令
                    channelModbus.debug("命令重试：Node:" + lastOutMsg.nodeId);
                    sendMsg(lastOutMsg);
                } else {
                    channelModbus.debug("重试失败！！！！：Node:" + lastOutMsg.nodeId);
                }
            }
            lastOutMsg = null;
        }

        // 当前的发送命令为空，发送下一条命令
        // 1、首先满足实时队列
        lastOutMsg = instantQueueCmd.poll();

        // 2、没有实时命令，发送定时命令
        if (lastOutMsg == null) {
            lastOutMsg = getNextQuery();
        }

        // 3、如果存在当前命令，就发送
        if (lastOutMsg != null) {
            sendCount++;
            checkCount = sendCount;
            channel.writeAndFlush(lastOutMsg);
        }
    }

    /**
     * 更新健康度
     *
     * @param h
     */
    public void goodHelth(ModbusMessage msg, Boolean h) {
        NodeRuntimeBean nodeRuntimeBean = channelModbus.nodeManager.getChannelNodeByNodeId(msg.nodeId);
        nodeRuntimeBean.goodHelth(msg, h);
    }

}

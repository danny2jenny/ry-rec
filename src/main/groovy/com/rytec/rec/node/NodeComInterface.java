package com.rytec.rec.node;

/**
 * Created by danny on 16-11-20.
 * Node 的通讯接口
 */
public interface NodeComInterface {

    /**
     * @param where  从哪里来的真 1 系统 2 联动 3 用户
     * @param nodeId node 的ID
     * @param cmd    命令  对应 util/CommandType
     * @param value  值
     * @return
     */

    // 生成一个消息对象
    Object genMessage(int where, int nodeId, int cmd, int value);

    /**
     * 收到的消息解码
     *
     * @param msg
     * @return 错误代码，没有错误是0
     */
    int decodeMessage(Object msg);

    /**
     * @param nodeMsg
     * @return
     */
    int sendMessage(NodeMessage nodeMsg);
}

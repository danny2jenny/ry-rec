package com.rytec.rec.node;


/**
 * Created by danny on 16-11-20.
 * Node 的通讯接口
 */

public interface NodeInterface {

    /**
     * 生成一个消息对象
     * @param where  从哪里来的真 1 系统 2 联动 3 用户
     * @param nodeId node 的ID
     * @param cmd    命令  对应 util/ConstantCommandType
     * @param value  值
     * @return
     */
    Object genMessage(int where, int nodeId, int cmd, int value);

    /**
     * 收到的消息解码，把ChannelMessage转换成NodeMessage给NodeManage使用
     *
     * @param msg
     * @return 错误代码，没有错误是0
     */
    void decodeMessage(Object msg);

    /**
     * 发送消息，一般用于控制
     * @param nodeMsg
     * @return
     */
    int sendMessage(NodeMessage nodeMsg);


    /**
     * 数据过滤函数，确定是否需要更新数据
     *
     * @param cfg    // Node 的配置对象
     * @param oldVal // 以前记录的值
     * @param newVal // 新值
     * @return //True 需要更新，False 不需要更新
     */
    boolean needUpdate(NodeConfig cfg, Object oldVal, Object newVal);


    /**
     * 节点的健康度
     * @param h
     */
    void goodHelth(Object msg, boolean h);
}

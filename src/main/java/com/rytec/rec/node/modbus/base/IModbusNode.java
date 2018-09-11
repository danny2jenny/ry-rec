package com.rytec.rec.node.modbus.base;


import com.rytec.rec.node.base.INodeBase;

/**
 * Created by danny on 16-11-20.
 * Modbus Node 的通讯接口
 */

public interface IModbusNode extends INodeBase {

    /**
     * 生成一个消息对象
     *
     * @param where  从哪里来的真 1 系统 2 联动 3 用户
     * @param nodeId node 的ID
     * @param cmd    命令  对应 util/ConstantCommandType
     * @param value  值
     * @return
     */
    Object genMessage(int where, int nodeId, int cmd, int regCount, int value);

    /**
     * 收到的消息解码，把ChannelMessage转换成NodeMessage给NodeManage使用
     *
     * @param msg
     * @return 错误代码，没有错误是0
     */
    void decodeMessage(Object msg);

    /**
     * 得到命令发送的间隔
     *
     * @return
     */
    int getInterval();
}

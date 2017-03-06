package com.rytec.rec.device;

import com.rytec.rec.cooperate.CooperateManager;
import com.rytec.rec.db.model.DeviceNode;
import com.rytec.rec.messenger.Message.WebMessage;
import com.rytec.rec.messenger.MessageType;
import com.rytec.rec.messenger.WebPush;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.ConstantErrorCode;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * Created by danny on 16-11-21.
 * 这个是Device的基类
 */
public class AbstractOperator {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CooperateManager cooperateManager;

    @Autowired
    public DeviceManager deviceManager;

    @Autowired
    NodeManager nodeManager;

    @Autowired
    WebPush webPush;

    /*
    * 设置输出的值
    */

    /**
     * 向Device的一个功能节点发送
     *
     * @param deviceId
     * @param fun
     * @param msg
     * @return
     */
    public int setValue(int deviceId, int fun, NodeMessage msg) {

        int rst = 0;

        //对应Function 的Node
        DeviceNode funNode = null;

        // 得到device所对应的Nodes
        HashMap<Integer, DeviceNode> deviceNodes = deviceManager.deviceNodeListByFun.get(deviceId);

        if (deviceNodes == null) {
            rst = ConstantErrorCode.DEVICE_NOT_FOUND;
        } else {

            // 找到功能对应的Node
            funNode = deviceNodes.get(fun);

            // 该功能是否有对应的Node
            if (funNode == null) {
                rst = ConstantErrorCode.DEVICE_FUN_NOT_CONFIG;
            } else {
                // 填充NodeID
                msg.node = funNode.getNid();
                NodeInterface nodeCom = nodeManager.getNodeComInterface(funNode.getNtype());
                if (nodeCom == null) {
                    rst = ConstantErrorCode.NODE_TYPE_NOTEXIST;
                } else {
                    rst = nodeCom.sendMessage(msg);
                }
            }
        }

        return rst;
    }


    /**
     * 一个功能节点的数据发生改变。
     * 普通的Device只有一个节点（default），
     * 1、写入到设备的状态
     * 2、向客户端广播状态改变
     *
     * @param deviceId
     * @param fun
     * @param oldValue
     * @param newValue
     */
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue) {
        DeviceRuntimeBean deviceRuntimeBean = deviceManager.deviceRuntimeList.get(deviceId).runtime;
        deviceRuntimeBean.state = newValue;
        WebMessage webMessage = new WebMessage();
        webMessage.type = MessageType.DEVICE_STATE;
        webMessage.msg = deviceRuntimeBean;
        webPush.clientBroadcast(webMessage);
    }

    /**
     * 对设备的操作
     *
     * @param device 设备编号
     * @param act    //操作码
     * @param parm   //参数
     * @return //返回代码：0为成功，非O请参考错误代码
     */
    public int operate(int from, int device, int act, Object parm) {
        return 0;
    }

    /**
     * 向联动发送状态改变的消息
     *
     * @param deviceId
     * @param sig
     * @param parm
     */
    public void sendSig(int deviceId, int sig, Object parm) {
        cooperateManager.onSignal(deviceId, sig, parm);
    }

    /**
     * 用来解析配置参数的，并生成配置对象。
     * 子类重载该函数
     *
     * @param config
     * @return
     */
    public Object parseConfig(String config) {
        return null;
    }

    public Object getConfig(int deviceId) {
        DeviceRuntimeConfigBean drb = deviceManager.deviceRuntimeList.get(deviceId);
        return drb.config;
    }

    // 产生一个该设备的状态对象，实现类需要覆盖
    public Object generateStateBean() {
        return null;
    }

    // 向客户端广播消息
    public void clientBroadcast(int type, Object body) {
        WebMessage webMessage = new WebMessage();
        webMessage.type = type;
        webMessage.msg = body;
        webPush.clientBroadcast(webMessage);
    }

}

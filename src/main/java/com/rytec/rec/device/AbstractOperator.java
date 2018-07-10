package com.rytec.rec.device;

import com.rytec.rec.app.RecBase;
import com.rytec.rec.cooperate.CooperateManager;
import com.rytec.rec.db.model.DeviceNode;
import com.rytec.rec.messenger.AlarmHub;
import com.rytec.rec.messenger.Message.WebMessage;
import com.rytec.rec.util.ConstantDeviceState;
import com.rytec.rec.util.ConstantMessageType;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.ConstantErrorCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * Created by danny on 16-11-21.
 * 这个是Device的基类
 */
public class AbstractOperator extends RecBase {

    @Autowired
    CooperateManager cooperateManager;

    @Autowired
    public DeviceManager deviceManager;

    @Autowired
    NodeManager nodeManager;

    @Autowired
    AlarmHub alarmHub;

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
            if (funNode == null)
                return ConstantErrorCode.DEVICE_FUN_NOT_EXIST;
            msg.node = funNode.getNid();

            // 发送消息
            rst = nodeManager.sendMsg(msg);
        }
        return rst;
    }

    /**
     * 生成节点消息
     *
     * @return
     */
    public NodeMessage genNodeMsg(int deviceId, int fun, NodeMessage msg) {

        //对应Function 的Node
        DeviceNode funNode = null;

        // 得到device所对应的Nodes
        HashMap<Integer, DeviceNode> deviceNodes = deviceManager.deviceNodeListByFun.get(deviceId);

        if (deviceNodes == null) {
            return null;
        } else {

            // 找到功能对应的Node
            funNode = deviceNodes.get(fun);
            if (funNode == null)
                return null;
            msg.node = funNode.getNid();

            // 返回消息
            return msg;
        }

    }

    /**
     * 一个功能节点的数据发生改变。
     * 普通的Device只有一个节点（default），
     * <p>
     * 需要处理newValue是null的情况
     * 1、写入到设备的状态
     * 2、向客户端广播状态改变
     *
     * @param deviceId
     * @param fun
     * @param oldValue
     * @param newValue
     */
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue, String unit) {
        DeviceRuntimeBean deviceRuntimeBean = deviceManager.getDeviceRuntimeList().get(deviceId);
        deviceRuntimeBean.runtime.state = newValue;

        if (newValue == null) {
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_OFFLINE;
        } else {
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_NORMAL;
        }

        clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
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
     * 发送告警
     *
     * @param device
     * @param sig
     * @param value
     */
    public void sendAlarm(int device, int sig, Object value) {
        alarmHub.processAlarm(device, sig, value);
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
        DeviceRuntimeBean drb = deviceManager.getDeviceRuntimeList().get(deviceId);
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
        webPush(webMessage);
    }

}

package com.rytec.rec.device;

import com.rytec.rec.cooperate.CooperateManager;
import com.rytec.rec.db.model.DeviceNode;
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
    DeviceManager deviceManager;

    @Autowired
    NodeManager nodeManager;

    /*
    * 设置输出的值
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


    /*
    * 当状态改变时，由通讯层的回调
    */
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue) {
    }


    /**
     * 改变设备的状态
     *
     * @param device
     * @param ste
     */
    public void setState(int device, int ste) {
        DeviceRuntimeBean drb = deviceManager.deviceRuntimeList.get(device);
        drb.state.state = ste;
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
        DeviceRuntimeBean drb = deviceManager.deviceRuntimeList.get(deviceId);
        return drb.config;
    }

}

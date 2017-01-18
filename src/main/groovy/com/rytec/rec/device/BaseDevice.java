package com.rytec.rec.device;

import com.rytec.rec.db.model.DeviceNode;
import com.rytec.rec.node.NodeComInterface;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.ErrorCode;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * Created by danny on 16-11-21.
 * 这个是Device的基类
 */
public class BaseDevice {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DeviceManager deviceManager;

    /*
    * 设置输出的值
    */
    public int setValue(int deviceId, int fun, NodeMessage msg) {

        int rst = ErrorCode.RST_SUCCESS;

        //对应Function 的Node
        DeviceNode funNode = null;

        // 得到device所对应的Nodes
        HashMap<Integer, DeviceNode> deviceNodes = deviceManager.deviceNodeList.get(deviceId);

        if (deviceNodes == null) {
            rst = ErrorCode.DEVICE_NOT_FOUND;
        } else {

            // 找到功能对应的Node
            for (DeviceNode node : deviceNodes.values()) {
                if (node.getNfun() == fun) {
                    funNode = node;
                    break;
                }
            }

            // 该功能是否有对应的Node
            if (funNode == null) {
                rst = ErrorCode.DEVICE_FUN_NOT_CONFIG;
            } else {
                // 填充NodeID
                msg.node = funNode.getNid();
                NodeComInterface nodeCom = NodeManager.getNodeComInterface(funNode.getNtype());
                if (nodeCom == null) {
                    rst = ErrorCode.NODE_TYPE_NOTEXIST;
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
    private void onValueChanged(int deviceId, int fun, NodeMessage msg) {
    }

}

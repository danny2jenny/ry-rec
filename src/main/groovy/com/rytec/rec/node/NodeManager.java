package com.rytec.rec.node;

import com.rytec.rec.bean.ChannelNode;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.device.DeviceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by danny on 16-11-21.
 * 1、管理所有的Node
 * 2、从数据库中提取Node的列表
 * 3、维护Node的状态
 * 4、当Node状态改变的时候，进行相应的通知
 * 5、通知到客户端，或者是告警
 */
@Service
public class NodeManager {

    @Autowired
    private DbConfig db;

    @Autowired
    DeviceManager deviceManager;

    //nid->ChannelNode 的一个Map
    private Map<Integer, ChannelNode> nodeMap = new HashMap();

    @PostConstruct
    private void initNodes() {
        List<ChannelNode> channelNodes = db.getChannelNodeList();

        for (ChannelNode cn : channelNodes) {
            cn.opt = new NodeOpt();
            nodeMap.put(cn.nid, cn);
        }

    }

    /*
    * 通讯层发来的数据
    * @id node 的id
    */
    public void onValue(int id, float value) {
        ChannelNode channelNode = nodeMap.get(id);
        float oldValue = channelNode.opt.value;
        if (oldValue != value) {
            //更新Node的值
            channelNode.opt.value = value;
            //向Device发送变化
            deviceManager.onValueChange(channelNode.device, channelNode.deviceFun, oldValue, value);
        }
    }

    public ChannelNode getChannelNodeByNodeId(int nodeId) {
        return nodeMap.get(nodeId);
    }


}

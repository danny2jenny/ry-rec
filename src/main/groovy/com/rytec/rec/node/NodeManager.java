/**
 * Created by danny on 16-11-21.
 * 1、管理所有的Node
 * 2、从数据库中提取Node的列表
 * 3、维护Node的状态
 * 4、当Node状态改变的时候，进行相应的通知
 * 5、通知到客户端，或者是告警
 */
package com.rytec.rec.node;

import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.util.NodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NodeManager {

    @Autowired
    private DbConfig db;

    @Autowired
    DeviceManager deviceManager;

    //nid->ChannelNode 的一个Map
    private Map<Integer, ChannelNodeState> channelNodeList = new HashMap();

    //node的实现对象列表
    @Autowired
    ApplicationContext context;

    // 具体Node的通讯实现接口对象
    static Map<Integer, Object> nodeComList = new HashMap();

    //得到一个Node的接口对象，通过 type
    public static NodeComInterface getNodeComInterface(int type) {
        return (NodeComInterface) nodeComList.get(type);
    }

    @PostConstruct
    private void initNodes() {

        // 初始化node的map
        List<ChannelNode> channelNodes = db.getChannelNodeList();

        for (ChannelNode cn : channelNodes) {
            // 给 ChannelNode 的运行数据初始化
            ChannelNodeState channelNodeState = new ChannelNodeState();
            channelNodeState.channelNode = cn;
            channelNodeState.nodeOpt = new NodeOpt();
            channelNodeList.put(cn.getNid(), channelNodeState);
        }

        // 初始化 node 接口实现
        Map<String, Object> nodes = context.getBeansWithAnnotation(NodeType.class);
        for (Object node : nodes.values()) {
            Class<? extends Object> nodeClass = node.getClass();
            NodeType annotation = nodeClass.getAnnotation(NodeType.class);
            nodeComList.put(annotation.value(), node);
        }
    }

    /*
    * 通讯层发来的数据
    * @id node 的id
    */
    public void onValue(int id, float value) {
        ChannelNodeState channelNodeState = channelNodeList.get(id);

        if (channelNodeState.nodeOpt == null) {
            channelNodeState.nodeOpt = new NodeOpt();
        }

        NodeOpt nodeOpt = channelNodeState.nodeOpt;

        float oldValue = nodeOpt.value;
        if (oldValue != value) {
            //更新Node的值
            nodeOpt.value = value;
            //向Device发送变化
            deviceManager.onValueChange(channelNodeState.channelNode.getDevice(), channelNodeState.channelNode.getDevicefun(), oldValue, value);
        }
    }

    public ChannelNodeState getChannelNodeByNodeId(int nodeId) {
        return channelNodeList.get(nodeId);
    }


}

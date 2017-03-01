/**
 * Created by danny on 16-11-21.
 * 1、管理所有的Node
 * 2、从数据库中提取Node的列表
 * 3、维护Node的状态
 * 4、当Node状态改变的时候，进行相应的通知
 * 5、通知到客户端，或者是告警
 */
package com.rytec.rec.node;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.AnnotationNodeType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Order(300)
public class NodeManager implements ManageableInterface {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DbConfig db;

    @Autowired
    DeviceManager deviceManager;

    //nid->ChannelNode 的一个Map
    private Map<Integer, NodeRuntimeBean> channelNodeList = new HashMap();


    @Autowired
    ApplicationContext context;

    // 具体Node的通讯实现接口对象
    private Map<Integer, NodeInterface> nodeComList = new ConcurrentHashMap();

    //得到一个Node的接口对象，通过 type
    public NodeInterface getNodeComInterface(int type) {
        return nodeComList.get(type);
    }

    public Map<Integer, NodeInterface> getAllNodeInterface() {
        return nodeComList;
    }


    private void initNodeInterface() {
        // 初始化 node 接口实现
        Map<String, Object> nodes = context.getBeansWithAnnotation(AnnotationNodeType.class);
        for (Object node : nodes.values()) {
            Class<? extends Object> nodeClass = node.getClass();
            AnnotationNodeType annotation = nodeClass.getAnnotation(AnnotationNodeType.class);
            nodeComList.put(annotation.value(), (NodeInterface) node);
        }
    }


    private void initConfig() {
        // 初始化node的map
        List<ChannelNode> channelNodes = db.getChannelNodeList();

        for (ChannelNode cn : channelNodes) {
            // 给 ChannelNode 的运行数据初始化
            NodeRuntimeBean nodeRuntimeBean = new NodeRuntimeBean();
            nodeRuntimeBean.channelNode = cn;                                      //ChannelNode
            nodeRuntimeBean.nodeState = new NodeState();                           //Node的状态
            nodeRuntimeBean.nodeConfig = BaseNode.parseConfig(cn.getNodeconf());   //Node的配置
            channelNodeList.put(cn.getNid(), nodeRuntimeBean);
        }
    }

    @PostConstruct
    private void init() {
        initConfig();
        initNodeInterface();
    }


    /*
    * 通讯层发来的数据
    * @id node 的id
    */
    public void onMessage(NodeMessage msg) {
        NodeRuntimeBean nodeRuntimeBean = channelNodeList.get(msg.node);

        NodeState nodeState = nodeRuntimeBean.nodeState;

        Object oldValue = nodeState.value;

        // 写命令
        // todo: 写命令的返回需要处理
        if (msg.type == ConstantCommandType.GENERAL_WRITE) {
            return;
        }

        NodeInterface nodeInterface = getNodeComInterface(nodeRuntimeBean.channelNode.getNtype());

        // 判断数据是否需要更新
        if (nodeInterface.valueCompare(nodeRuntimeBean.nodeConfig, oldValue, msg.value)) {
            // 数据需要更新
            nodeState.value = msg.value;
            logger.debug("Node:" + msg.node + ':' + oldValue + ':' + msg.value);
            // Device 可能指针为空
            Integer deviceId = nodeRuntimeBean.channelNode.getDevice();
            if (deviceId == null) {
                return;
            }
            deviceManager.onValueChange(deviceId, nodeRuntimeBean.channelNode.getDevicefun(), oldValue, msg.value);
        } else {
            // 数据不需要更新
        }
    }

    public NodeRuntimeBean getChannelNodeByNodeId(int nodeId) {
        return channelNodeList.get(nodeId);
    }


    public void stop() {
        channelNodeList.clear();
    }

    public void start() {
        initConfig();
    }

}

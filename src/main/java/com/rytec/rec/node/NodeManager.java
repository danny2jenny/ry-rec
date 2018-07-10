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
import com.rytec.rec.db.model.NodeRedirect;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.node.modbus.base.BaseModbusNode;
import com.rytec.rec.node.modbus.base.ModbusNodeInterface;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.AnnotationNodeType;
import com.rytec.rec.util.ConstantErrorCode;
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

    @Autowired
    private DbConfig db;

    @Autowired
    DeviceManager deviceManager;

    //nid->ChannelNode 的一个Map
    private Map<Integer, NodeRuntimeBean> channelNodeList = new HashMap();


    // NodeRedirect
    // 两级 Hash
    // nodeid -> map
    // nodeRedirectId -> NodeRedirect
    public HashMap<Integer, HashMap> nodeRedirectList = new HashMap();


    @Autowired
    ApplicationContext context;

    // 具体Node的通讯实现接口对象
    private Map<Integer, ModbusNodeInterface> nodeComList = new ConcurrentHashMap();

    //得到一个Node的接口对象，通过 type
    public ModbusNodeInterface getNodeComInterface(int type) {
        return nodeComList.get(type);
    }

    public Map<Integer, ModbusNodeInterface> getAllNodeInterface() {
        return nodeComList;
    }


    private void initNodeInterface() {
        // 初始化 node 接口实现
        Map<String, Object> nodes = context.getBeansWithAnnotation(AnnotationNodeType.class);
        for (Object node : nodes.values()) {
            Class<? extends Object> nodeClass = node.getClass();
            AnnotationNodeType annotation = nodeClass.getAnnotation(AnnotationNodeType.class);
            nodeComList.put(annotation.value(), (ModbusNodeInterface) node);
        }
    }


    private void initConfig() {
        // 初始化node的map
        List<ChannelNode> channelNodes = db.getChannelNodeList();

        for (ChannelNode cn : channelNodes) {
            // 给 ChannelNode 的运行数据初始化
            NodeRuntimeBean nodeRuntimeBean = new NodeRuntimeBean(this.nodeComList.get(cn.getNtype()));
            nodeRuntimeBean.channelNode = cn;                                      //ChannelNode
            nodeRuntimeBean.nodeState = new NodeState();                           //Node的状态
            nodeRuntimeBean.nodeConfig = BaseModbusNode.parseConfig(cn.getNodeconf());   //Node的配置
            channelNodeList.put(cn.getNid(), nodeRuntimeBean);
        }

        // 填充NodeRedirect 的初始值
        List<NodeRedirect> nodeRedirects = db.getNodeRedirectList();

        for (NodeRedirect nr : nodeRedirects) {
            HashMap<Integer, NodeRedirect> nodeRedirectItemList = nodeRedirectList.get(nr.getNode());
            if (nodeRedirectItemList == null) {
                nodeRedirectItemList = new HashMap();
                nodeRedirectList.put(nr.getNode(), nodeRedirectItemList);
            }
            nodeRedirectItemList.put(nr.getId(), nr);
        }
    }

    @PostConstruct
    private void init() {
        initNodeInterface();
    }


    /*
     * 通讯层发来的数据
     * @id node 的id
     */
    public void onMessage(NodeMessage msg) {
        NodeRuntimeBean nodeRuntimeBean = channelNodeList.get(msg.node);

        // todo: 为什么nodeRuntimeBean可能为空，可能顺序不对
        if (nodeRuntimeBean == null) {
            return;
        }

        NodeState nodeState = nodeRuntimeBean.nodeState;

        Object oldValue = nodeState.value;

        // 写命令
        if (msg.type == ConstantCommandType.GENERAL_WRITE) {
            return;
        }

        ModbusNodeInterface modbusNodeInterface = getNodeComInterface(nodeRuntimeBean.channelNode.getNtype());

        // 判断数据是否需要更新
        if (modbusNodeInterface.needUpdate(nodeRuntimeBean.nodeConfig, oldValue, msg.value)) {
            // 数据需要更新
            nodeState.value = msg.value;

            // Device 可能指针为空
            Integer deviceId = nodeRuntimeBean.channelNode.getDevice();
            if (deviceId != null) {
                deviceManager.onValueChange(
                        deviceId,
                        nodeRuntimeBean.channelNode.getDevicefun(),
                        oldValue,
                        msg.value,
                        nodeRuntimeBean.nodeConfig.unit
                );
            }

            // 节点重新定向
            Map<Integer, NodeRedirect> nodeRedirectMap = nodeRedirectList.get(msg.node);
            if (nodeRedirectMap != null) {
                for (NodeRedirect nr : nodeRedirectMap.values()) {
                    deviceManager.onValueChange(
                            nr.getDevice(),
                            nr.getDevicefun(),
                            oldValue,
                            msg.value,
                            nodeRuntimeBean.nodeConfig.unit
                    );
                }
            }
        }
    }

    public int sendMsg(NodeMessage msg) {

        int rst;

        // 找到node的信息
        NodeRuntimeBean nodeRuntimeBean = channelNodeList.get(msg.node);
        if (nodeRuntimeBean == null)
            return ConstantErrorCode.DEVICE_FUN_NOT_CONFIG;

        ModbusNodeInterface nodeCom = getNodeComInterface(nodeRuntimeBean.channelNode.getNtype());
        if (nodeCom == null) {
            rst = ConstantErrorCode.NODE_TYPE_NOTEXIST;
        } else {
            rst = nodeCom.sendMessage(msg);
        }

        return rst;
    }

    // 得到Node的运行状态
    public NodeRuntimeBean getChannelNodeByNodeId(int nodeId) {
        return channelNodeList.get(nodeId);
    }


    public void stop() {
        channelNodeList.clear();
        nodeRedirectList.clear();
    }

    public void start() {
        initConfig();
    }

}

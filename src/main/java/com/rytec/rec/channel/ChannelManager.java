package com.rytec.rec.channel;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.AnnotationChannelType;
import com.rytec.rec.util.ConstantFromWhere;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 所有的Channel的管理
 * Created by danny on 16-12-16.
 */

@Service
@Order(300)
public class ChannelManager implements ManageableInterface {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NodeManager nodeManager;

    @Autowired
    ApplicationContext context;

    @Autowired
    DbConfig dbConfig;

    // Channel 接口对象的列表
    private HashMap<Integer, ChannelInterface> channelInterfaceMap = new HashMap<>();

    /*
     * Channel 和 Node 的关系
     *
     * 第一级：cid->hash
     * 第二级：nid->channelnode
     */
    public HashMap<Integer, HashMap> channelNodes = new HashMap<>();


    private void initChannelInterface() {
        Map<String, Object> channels = context.getBeansWithAnnotation(AnnotationChannelType.class);
        for (Object channel : channels.values()) {
            Class<? extends Object> channelClass = channel.getClass();
            AnnotationChannelType annotation = channelClass.getAnnotation(AnnotationChannelType.class);
            channelInterfaceMap.put(annotation.value(), (ChannelInterface) channel);
        }
    }

    private void initConfig() {
        // 建立Channel和Node的关系
        List<ChannelNode> chaNodeList = dbConfig.getChannelNodeList();
        //第一级的Map cid-> map
        for (ChannelNode cn : chaNodeList) {
            int channelId = cn.getId();
            HashMap<Integer, ChannelNode> cha = channelNodes.get(channelId);
            //不存在，建立该Channel
            if (cha == null) {
                cha = new HashMap();
                this.channelNodes.put(channelId, cha);
            }
            cha.put(cn.getNid(), cn);
        }

    }

    @PostConstruct
    void init() {
        initChannelInterface();
        initConfig();
    }

    // 得到一个Channel的Interface
    public ChannelInterface getChannelInterface(int type) {
        return channelInterfaceMap.get(type);
    }

    // 得到所有Channel的接口
    public HashMap<Integer, ChannelInterface> getAllChannelInterface() {
        return channelInterfaceMap;
    }


    public void stop() {
        channelNodes.clear();
    }

    public void start() {
        initConfig();
    }


    /**
     * 通道连接
     *
     * @param cid
     */
    public void channelOnline(int cid, boolean online) {
        // todo: 向消息中心发送通知

        // 更新该Channel上的每一个节点的数据
        NodeMessage nodeMessage = new NodeMessage();
        nodeMessage.from = ConstantFromWhere.FROM_SYSTEM;
        nodeMessage.value = null;
        HashMap<Integer, ChannelNode> channelNodeMap = channelNodes.get(cid);
        for (ChannelNode cn : channelNodeMap.values()) {
            nodeMessage.node = cn.getNid();
            nodeManager.onMessage(nodeMessage);
        }
    }

}

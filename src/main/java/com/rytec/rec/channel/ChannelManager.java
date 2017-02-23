package com.rytec.rec.channel;

import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.util.AnnotationChannelType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
public class ChannelManager {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ApplicationContext context;

    @Autowired
    DbConfig dbConfig;

    // Channel 接口对象的列表
    private HashMap<Integer, ChannelInterface> channelInterfaceMap = new HashMap();

    /*
     * Channel 和 Node 的关系
     */
    public HashMap<Integer, HashMap> channelNodes = new HashMap();

    @PostConstruct
    private void init() {
        Map<String, Object> channels = context.getBeansWithAnnotation(AnnotationChannelType.class);
        for (Object channel : channels.values()) {
            Class<? extends Object> channelClass = channel.getClass();
            AnnotationChannelType annotation = channelClass.getAnnotation(AnnotationChannelType.class);
            channelInterfaceMap.put(annotation.value(), (ChannelInterface) channel);
        }

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

    // 得到一个Channel的Interface
    public ChannelInterface getChannelInterface(int type) {
        return channelInterfaceMap.get(type);
    }

    // 得到所有Channel的接口
    public HashMap<Integer, ChannelInterface> getAllChannelInterface() {
        return channelInterfaceMap;
    }

}

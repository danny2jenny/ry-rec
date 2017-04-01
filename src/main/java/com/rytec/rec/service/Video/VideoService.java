package com.rytec.rec.service.Video;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.Channel;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.messenger.MqttService;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Danny on 2017/3/13.
 * <p>
 * 与 video servie 进行通讯的接口
 */

@Service
@Order(300)
public class VideoService implements ManageableInterface {

    @Autowired
    DbConfig dbConfig;

    @Autowired
    public NodeManager nodeManager;

    private MqttService mqttService;

    public void setMqttService(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    /**
     * 初始化对应的HashMap
     * 两级 HashMap
     * 第一级：channelId->Map
     * 第二级：adr->ChannelNode
     */
    public HashMap<Integer, HashMap> channelAdrMapList = new HashMap();

    // nid -> channelNode
    public HashMap<Integer, ChannelNode> nodeMapList = new HashMap();


    @PostConstruct
    private void initConfig() {
        List<ChannelNode> chaNodeList = dbConfig.getChannelNodeList();


        for (ChannelNode cn : chaNodeList) {
            // 只管理 2000~3000 的channel
            if ((cn.getCtype() < 2000) || (cn.getCtype() > 3000)) {
                continue;
            }

            HashMap<Integer, ChannelNode> adrMapList = channelAdrMapList.get(cn.getId());
            if (adrMapList == null) {
                adrMapList = new HashMap();
            }

            adrMapList.put(cn.getAdr(), cn);

            nodeMapList.put(cn.getNid(), cn);
        }

    }

    /**
     * 发送消息
     *
     * @param msg
     * @return
     */
    public int sendMsg(Object msg) {

        NodeMessage sendMsg = (NodeMessage) msg;

        ChannelNode channelNode = nodeManager.getChannelNodeByNodeId(sendMsg.node).channelNode;

        //通过 node 找到 channel 和 add，然后发送给RyTcpServer
        //ChannelNode channelNode = nodeMapList.get(msg.nodeId);
        String payload = "" + 3 + ',' + channelNode.getId() + ',' + channelNode.getAdr() + ',' + sendMsg.value;
        mqttService.videoCmd(ConstantVideo.VIDEO_PTZ, payload);

        return 0;
    }

    /**
     * 得到视频设备的配置字符串
     * ChannelId         IP      Port        Username           Password         Type
     *
     * @return
     */
    public String getConfig() {

        List<Channel> channels = dbConfig.getChannelList();

        String cfg = "";

        for (Channel cn : channels) {
            if (cn.getType() < 2000 || cn.getType() > 3000) {
                continue;
            }
            cfg = cfg
                    + cn.getId() + ','
                    + cn.getIp() + ','
                    + cn.getPort() + ','
                    + cn.getLogin() + ','
                    + cn.getPass() + ','
                    + cn.getType() + ';';
        }

        return cfg;
    }

    @Override
    public void stop() {
        channelAdrMapList.clear();
        nodeMapList.clear();
    }


    @Override
    public void start() {
        initConfig();   // 初始化配置
        mqttService.videoCmd(ConstantVideo.VIDEO_INIT, getConfig());
    }


}

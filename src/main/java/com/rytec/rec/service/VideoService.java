package com.rytec.rec.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.Channel;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.messenger.Message.MqttMessage;
import com.rytec.rec.messenger.MqttService;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.ConstantVideo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
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

    ObjectMapper objectMapper = new ObjectMapper();

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
     * PTZ 控制
     *
     * @param msg
     * @return
     */
    public int ptzControl(Object msg) {
        NodeMessage sendMsg = (NodeMessage) msg;

        ChannelNode channelNode = nodeManager.getChannelNodeByNodeId(sendMsg.node).channelNode;

        //通过 node 找到 channel 和 add，然后发送给RyTcpServer
        //ChannelNode channelNode = nodeMapList.get(msg.nodeId);

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.cmd = ConstantVideo.VIDEO_PTZ;
        PayloadPtz payload = new PayloadPtz();
        payload.ptzCmd = ConstantVideo.PTZ_GOTO_PRESET;
        payload.nvr = channelNode.getId();
        payload.channel = channelNode.getAdr();

        if (sendMsg.value instanceof String){
            payload.ptz = Integer.parseInt((String) sendMsg.value);
        }

        if (sendMsg.value instanceof Integer){
            payload.ptz = (Integer) sendMsg.value;
        }


        mqttMessage.payload = payload;

        try {
            mqttService.sendMsg(ConstantVideo.TOPIC_VIDEO_SERVICE, objectMapper.writeValueAsString(mqttMessage));
        } catch (JsonProcessingException e) {
        }

        return 0;
    }


    /**
     * 收到VideoServie的消息
     */
    public void onVideoMessage(String str) {
        MqttMessage msg;
        try {
            msg = objectMapper.readValue(str, MqttMessage.class);
        } catch (IOException e) {
            return;
        }

        switch (msg.cmd) {
            // 请求初始化
            case ConstantVideo.VIDEO_INIT_REQUEST:
                sendVideoServiceConfig();
        }
    }

    /**
     * 得到VideoChannel的配置对象
     *
     * @return
     */
    private List<Channel> getVideoChannelConfig() {

        List<Channel> nvrs = new ArrayList<>();

        List<Channel> channels = dbConfig.getChannelList();

        for (Channel cn : channels) {
            if (cn.getType() < 2000 || cn.getType() > 3000) {
                continue;
            }
            nvrs.add(cn);
        }

        return nvrs;
    }

    /**
     * 向VideoService发送配置对象
     */
    public void sendVideoServiceConfig() {
        MqttMessage msg = new MqttMessage();
        msg.cmd = ConstantVideo.VIDEO_INIT;
        msg.payload = getVideoChannelConfig();
        try {
            mqttService.sendMsg(ConstantVideo.TOPIC_VIDEO_SERVICE, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
        }
    }

    @Override
    public void stop() {
        channelAdrMapList.clear();
        nodeMapList.clear();
    }


    @Override
    public void start() {
        initConfig();   // 初始化配置
        try {
            sendVideoServiceConfig();
        } catch (Exception e) {

        }
    }
}

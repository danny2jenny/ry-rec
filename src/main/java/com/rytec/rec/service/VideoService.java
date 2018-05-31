package com.rytec.rec.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.Channel;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.messenger.Message.MqttMsg;
import com.rytec.rec.messenger.Message.WebMessage;
import com.rytec.rec.messenger.MqttService;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.ConstantFromWhere;
import com.rytec.rec.util.ConstantMessageType;
import com.rytec.rec.util.ConstantMqtt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

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
public class VideoService extends RecBase implements ManageableInterface {

    @Autowired
    DbConfig dbConfig;

    @Autowired
    public NodeManager nodeManager;

    @Autowired
    ChannelManager channelManager;

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

        MqttMsg mqttMsg = new MqttMsg();
        mqttMsg.cmd = ConstantMqtt.VIDEO_PTZ;
        PayloadPtz payload = new PayloadPtz();
        payload.ptzCmd = ConstantMqtt.PTZ_GOTO_PRESET;
        payload.nvr = channelNode.getId();
        payload.channel = channelNode.getAdr();

        if (sendMsg.value instanceof String) {
            payload.ptz = Integer.parseInt((String) sendMsg.value);
        }

        if (sendMsg.value instanceof Integer) {
            payload.ptz = (Integer) sendMsg.value;
        }


        mqttMsg.payload = payload;

        // 直接发送给客户端
        webNotify(ConstantMessageType.VIDEO_PTZ, payload);
        return 0;

//
//        try {
//            mqttService.sendMsg(ConstantMqtt.TOPIC_VIDEO_SERVICE, objectMapper.writeValueAsString(mqttMsg));
//        } catch (JsonProcessingException e) {
//        }
//
//        return 0;
    }


    /**
     * 收到VideoServie的消息
     */
    public void onVideoMessage(String str) {
        int cmd = 0;
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readValue(str, JsonNode.class);
            cmd = jsonNode.path("cmd").asInt();
        } catch (IOException e) {
            return;
        }

        switch (cmd) {
            // 请求初始化
            case ConstantMqtt.VIDEO_INIT_REQUEST:
                sendVideoServiceConfig();
                break;
            // NVR 链接状态
            case ConstantMqtt.VIDEO_CHANNEL_ONLINE:
                int channel = jsonNode.path("channel").asInt();
                boolean online = jsonNode.path("online").asBoolean();
                if (online) {
                    NodeMessage nodeMessage = new NodeMessage();
                    nodeMessage.from = ConstantFromWhere.FROM_SYSTEM;
                    nodeMessage.value = 1;
                    HashMap<Integer, ChannelNode> channelNodeMap = channelManager.channelNodes.get(channel);
                    for (ChannelNode cn : channelNodeMap.values()) {
                        nodeMessage.node = cn.getNid();
                        nodeManager.onMessage(nodeMessage);
                    }
                } else {
                    channelManager.channelOffline(channel);
                }
                break;
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

        // 2001~3000 是视频的channel
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
        MqttMsg msg = new MqttMsg();
        msg.cmd = ConstantMqtt.VIDEO_INIT;
        msg.payload = getVideoChannelConfig();
        try {
            mqttService.sendMsg(ConstantMqtt.TOPIC_VIDEO_SERVICE, objectMapper.writeValueAsString(msg));
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

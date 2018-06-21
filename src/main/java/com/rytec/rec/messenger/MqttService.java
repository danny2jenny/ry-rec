package com.rytec.rec.messenger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.service.IEC61850Service;
import com.rytec.rec.service.VideoService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created by Danny on 2017/3/17.
 */

@Service
public class MqttService {

    @Autowired
    VideoService videoService;

    @Autowired
    IEC61850Service iec61850Service;

    @Resource
    private MqttPahoMessageHandler mqtt;

    ObjectMapper objectMapper = new ObjectMapper();

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 让 videoService 可以找到该服务
     */
    @PostConstruct
    void init() {
        videoService.setMqttService(this);
    }

    /**
     * MQTT收到的Video消息
     *
     * @param message
     */
    public void onMqttMessage(String message) {
        int cmd = 0;
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readValue(message, JsonNode.class);
            cmd = jsonNode.path("cmd").asInt();
        } catch (IOException e) {
            return;
        }

        if (cmd <= 100) {
            videoService.onVideoMessage(message);
        } else {
            iec61850Service.onMqttMessage(message);
        }

    }

    /**
     * 所有发送的消息
     *
     * @param topic
     * @param payload
     */
    public void sendMsg(String topic, String payload) {
        Message<String> message = MessageBuilder.withPayload(payload).setHeader(MqttHeaders.TOPIC, topic).build();
        mqtt.handleMessage(message);
    }

}
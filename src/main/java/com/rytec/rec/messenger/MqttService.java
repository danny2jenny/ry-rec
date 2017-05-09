package com.rytec.rec.messenger;

import com.rytec.rec.util.ConstantVideo;
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

/**
 * Created by Danny on 2017/3/17.
 */

@Service
public class MqttService {

    @Autowired
    VideoService videoService;

    @Resource
    private MqttPahoMessageHandler mqtt;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     *
     */
    @PostConstruct
    void init(){
       videoService.setMqttService(this);
    }

    /**
     * MQTT收到的Video消息
     *
     * @param message
     */
    public void onVideoMessage(String message) {
        videoService.onVideoMessage(message);
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

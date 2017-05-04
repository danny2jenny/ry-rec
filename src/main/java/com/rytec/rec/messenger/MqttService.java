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
        // 解析命令
        int p1 = -1;
        int p2 = -1;
        p1 = message.indexOf('<');
        p2 = message.indexOf('>');

        int cmd = 0;
        if (p1 >= 0 && p2 > 0 && p2 > p1) {
            String cmd_str = message.substring(p1 + 1, p2);
            cmd = Integer.parseInt(cmd_str);
        } else {
            return;
        }

        switch (cmd) {
            // 初始化请求
            case ConstantVideo.VIDEO_INIT_REQUEST:
                // 发送Video配置
                videoCmd(ConstantVideo.VIDEO_INIT, videoService.getConfig());
                break;
            // NVR 消息 todo: 解析Video消息
            case ConstantVideo.VIDEO_INIT_INFO:

                break;
        }

        logger.debug(message);
    }


    /**
     * 发送消息
     *
     * @param topic
     * @param payload
     */
    private void sendMsg(String topic, String payload) {
        Message<String> message = MessageBuilder.withPayload(payload).setHeader(MqttHeaders.TOPIC, topic).build();
        mqtt.handleMessage(message);
    }

    /**
     * 向Video发送消息
     *
     * @param cmd
     * @param payload
     */
    public void videoCmd(int cmd, String payload) {
        String msg;
        msg = "<" + cmd + ">" + payload;
        sendMsg(ConstantVideo.TOPIC_VIDEO_SERVICE, msg);
    }
}

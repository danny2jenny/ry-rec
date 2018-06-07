package com.rytec.rec.messenger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.service.IEC61850Service;
import com.rytec.rec.service.VideoService;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class MqttServerListener extends AbstractInterceptHandler {

    @Value("${mq.sub.video}")
    String subscribe;

    @Autowired
    VideoService videoService;

    @Autowired
    IEC61850Service iec61850Service;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getID() {
        return "RYTEC MQTT BROKER";
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
        String topic = msg.getTopicName();

        if (subscribe.equals(topic)){
            String body = msg.getPayload().toString(UTF_8);


            int cmd = 0;
            JsonNode jsonNode;
            try {
                jsonNode = objectMapper.readValue(body, JsonNode.class);
                cmd = jsonNode.path("cmd").asInt();
            } catch (IOException e) {
                return;
            }

            if (cmd <= 100) {
                videoService.onVideoMessage(body);
            } else {
                iec61850Service.onMqttMessage(body);
            }
        }
    }


}

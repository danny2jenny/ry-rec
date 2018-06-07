package com.rytec.rec.messenger;

import com.rytec.rec.app.RecBase;
import io.moquette.interception.InterceptHandler;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * MQTT 的Java Broker
 * https://github.com/andsel/moquette/blob/master/embedding_moquette/src/main/java/io/moquette/testembedded/EmbeddedLauncher.java
 */
@Service
public class MqttServer extends RecBase {

    final Server mqttBroker = new Server();

    @Autowired
    MqttServerListener mqttServerListener;

    @PostConstruct
    public void Start(){
        Properties properties = new Properties();
        MemoryConfig config = new MemoryConfig(properties);
        properties.setProperty("port", "1883");
        properties.setProperty("host", "0.0.0.0");
        properties.setProperty("allow_anonymous", "true");

        List<? extends InterceptHandler> userHandlers = Collections.singletonList(mqttServerListener);

        try {
            mqttBroker.startServer(config, userHandlers);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @PreDestroy
    public void Stop(){
        mqttBroker.stopServer();
    }

    /**
     * 发送消息
     * @param topic
     * @param payload
     */
    public void sendMsg(String topic, String payload){
        MqttPublishMessage message = MqttMessageBuilders.publish()
                .topicName(topic)
                .retained(true)
                .qos(MqttQoS.EXACTLY_ONCE)
                .payload(Unpooled.copiedBuffer(payload.getBytes(UTF_8)))
                .build();

        mqttBroker.internalPublish(message, "RYTEC MQTT BROKER");
    }

}

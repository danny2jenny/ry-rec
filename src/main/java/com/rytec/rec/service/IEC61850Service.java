package com.rytec.rec.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.messenger.Message.MqttMsg;
import com.rytec.rec.messenger.MqttService;
import com.rytec.rec.util.ConstantMqtt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by danny on 17-6-17.
 */
@Service
@Order(400)
public class IEC61850Service implements ManageableInterface {

    private List<DeviceRuntimeBean> iec61850runtime = new ArrayList();

    @Autowired
    DeviceManager deviceManager;

    @Autowired
    MqttService mqttService;

    ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void initConfig() {
        // 添加需要61850接口的device
        for (DeviceRuntimeBean deviceRuntimeBean : deviceManager.deviceRuntimeList.values()) {
            if (deviceRuntimeBean.device.getIec61850() > 0) {
                iec61850runtime.add(deviceRuntimeBean);
            }
        }
    }

    /**
     * 接收到MQTT消息
     *
     * @param msg
     */
    public void onMqttMessage(String msg) {

        int cmd = 0;
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readValue(msg, JsonNode.class);
            cmd = jsonNode.path("cmd").asInt();
        } catch (IOException e) {
            return;
        }

        switch (cmd) {
            case ConstantMqtt.IEC61850_INIT_REQUEST:
                sendServieConfig();
                break;
        }

    }

    /**
     * 发送配置
     */
    private void sendServieConfig() {
        MqttMsg msg = new MqttMsg();
        msg.cmd = ConstantMqtt.IEC61850_INIT;
        msg.payload = iec61850runtime;
        try {
            mqttService.sendMsg(ConstantMqtt.TOPIC_VIDEO_SERVICE, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
        }
    }

    /**
     * 更新一个device的状态
     *
     * @param runtimeBean
     */
    public void update(DeviceRuntimeBean runtimeBean) {
        MqttMsg msg = new MqttMsg();
        msg.cmd = ConstantMqtt.IEC61850_UPDATE;
        msg.payload = runtimeBean;
        try {
            mqttService.sendMsg(ConstantMqtt.TOPIC_VIDEO_SERVICE, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
        }
    }

    @Override
    public void stop() {
        iec61850runtime.clear();
    }

    @Override
    public void start() {
        initConfig();
        sendServieConfig();
    }
}

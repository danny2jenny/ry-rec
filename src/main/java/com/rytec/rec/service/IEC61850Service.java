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
import com.rytec.rec.app.RecBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by danny on 17-6-17.
 */
@Service
@Lazy
@Order(400)
public class IEC61850Service extends RecBase implements ManageableInterface {

    private List<DeviceRuntimeBean> iec61850runtime = new ArrayList();

    @Autowired
    DeviceManager deviceManager;

    @Autowired
    MqttService mqttService;

    ObjectMapper objectMapper = new ObjectMapper();

    private void initConfig() {
        // 添加需要61850接口的device
        for (DeviceRuntimeBean deviceRuntimeBean : deviceManager.getDeviceRuntimeList().values()) {
            if (deviceRuntimeBean.device.getIec61850() > 0) {
                iec61850runtime.add(deviceRuntimeBean);
            }
        }
        logger.debug("61850Manager Init...");
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
        /**
         * 因为循环依赖的关系，在发送配置前，先得到61850的设备
         */
        if (iec61850runtime.size()==0){
            initConfig();
        }
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
     * @param runtimeBean 首先判断 iec61850runtime 中是否有该device
     */
    public void update(DeviceRuntimeBean runtimeBean) {

        if (!iec61850runtime.contains(runtimeBean)) {
            return;
        }

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

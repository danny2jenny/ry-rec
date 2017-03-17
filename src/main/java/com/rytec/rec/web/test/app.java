package com.rytec.rec.web.test;

import com.rytec.rec.app.AppManager;
import com.rytec.rec.messenger.WebPush;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


/**
 * Created by Danny on 2017/3/5.
 */

@Controller
public class app {

    @Autowired
    AppManager appManager;

    @Autowired
    WebPush webPush;

    @GetMapping("/system")
    @ResponseBody
    public void system() {
        appManager.systemReload();
    }



    @Resource
    private MqttPahoMessageHandler mqtt;

    @RequestMapping("msg")
    @ResponseBody
    public String msg() {
        //webPush.clientBroadcast(new WebMessage());
        Message<String> message = MessageBuilder.withPayload("==========1111111111111111111111111=========").setHeader(MqttHeaders.TOPIC, "sensor").build();
        mqtt.handleMessage(message);

        return "OK";
    }

}

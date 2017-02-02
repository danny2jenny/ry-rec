package com.rytec.rec.device.operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.messenger.Message.MsgDeviceState;
import com.rytec.rec.messenger.MessageType;
import com.rytec.rec.messenger.WebPush;
import com.rytec.rec.util.Description;
import com.rytec.rec.util.DeviceType;
import com.rytec.rec.util.ConstantDeviceState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-12-16.
 * 输入设备
 */
@Service
@DeviceType(102)
@Description("开关输入")
public class Input extends AbstractOperator {

    @Autowired
    WebPush webPush;

    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue) {

        // 向客户端广播状态改变
        MsgDeviceState msgDeviceState = new MsgDeviceState();
        msgDeviceState.state.device = deviceId;
        msgDeviceState.msg_no = MessageType.DEVICE_STATE;

        if ((Boolean) newValue == true) {
            msgDeviceState.state.state = ConstantDeviceState.STATE_ON;
        } else {
            msgDeviceState.state.state = ConstantDeviceState.STATE_OFF;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        try {
            json = objectMapper.writeValueAsString(msgDeviceState);
        } catch (JsonProcessingException e) {
            json = "";
        }

        webPush.clientBroadcast(json);
    }
}

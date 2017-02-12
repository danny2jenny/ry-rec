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

    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue) {

        if ((Boolean) newValue == true) {
            setState(deviceId, ConstantDeviceState.STATE_ON);
        } else {
            setState(deviceId, ConstantDeviceState.STATE_OFF);
        }

    }
}

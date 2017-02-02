package com.rytec.rec.device.operator;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.messenger.Message.MsgDeviceState;
import com.rytec.rec.messenger.MessageType;
import com.rytec.rec.messenger.WebPush;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-11-29.
 * 输出
 * 三个端口：
 * 101:control：开启、关闭
 * 102:feedback：辅助节点，判断开启、关闭是否成功
 * 103:remote：本地、远程
 */
@Service
@DeviceType(101)
@Description("远程开关")
public class Output extends AbstractOperator {

    @Autowired
    WebPush webPush;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 开关控制
     *
     * @param deviceId
     * @param from
     * @param value
     */
    public int setSwitch(int deviceId, int from, Boolean value) {
        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = from;
        nodeMsg.type = ConstantCommandType.GENERAL_WRITE;
        nodeMsg.value = value;
        return this.setValue(deviceId, ConstantDeviceFunction.DEV_FUN_PORT_MAIN, nodeMsg);
    }

    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue) {
        // 向客户端广播状态改变
        MsgDeviceState msgDeviceState = new MsgDeviceState();
        msgDeviceState.msg_no = MessageType.DEVICE_STATE;
        msgDeviceState.state.device = deviceId;

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

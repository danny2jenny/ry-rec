package com.rytec.rec.messenger;

import com.rytec.rec.db.mapper.AlarmMapper;
import com.rytec.rec.db.mapper.AlarmVideoMapper;
import com.rytec.rec.db.model.Alarm;
import com.rytec.rec.db.model.AlarmVideoExample;
import com.rytec.rec.messenger.Message.AlarmMessage;
import com.rytec.rec.messenger.Message.WebMessage;
import com.rytec.rec.util.ConstantMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by 12793 on 2017/4/14.
 */
@Service
public class AlarmHub {

    /**
     * 处理告警
     *
     * @param deviceRuntimeBean
     */

    @Autowired
    AlarmMapper alarmMapper;

    @Autowired
    WebPush webPush;

    @Autowired
    AlarmVideoMapper alarmVideoMapper;

    WebMessage webMessage = new WebMessage();

    /**
     * 告警处理
     *
     * @param device
     * @param sig
     * @param value
     */
    public void processAlarm(int device, int sig, Object value) {

        // 告警写入数据库
        Alarm alarm = new Alarm();
        alarm.setDevice(device);
        alarm.setSig(sig);

        alarm.setTime(new Date());
        if (value != null) {
            alarm.setValue(value.toString());
        }

        alarmMapper.insert(alarm);

        // 生成告警消息
        AlarmMessage alarmMessage = new AlarmMessage();
        alarmMessage.alarmRec = alarm;
        AlarmVideoExample alarmVideoExample = new AlarmVideoExample();
        alarmVideoExample.createCriteria().andDeviceEqualTo(device).andSigEqualTo(sig);
        alarmMessage.actions = alarmVideoMapper.selectByExample(alarmVideoExample);

        webMessage.type = ConstantMessageType.DEVICE_ALARM;
        webMessage.msg = alarmMessage;
        webPush.clientBroadcast(webMessage);
    }
}

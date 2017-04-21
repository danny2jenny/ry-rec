package com.rytec.rec.messenger.Message;

import com.rytec.rec.db.model.Alarm;
import com.rytec.rec.db.model.AlarmVideo;

import java.util.List;

/**
 * Created by 12793 on 2017/4/20.
 * 告警推送消息
 */
public class AlarmMessage {
    public Alarm alarmRec;
    public List<AlarmVideo> actions;
}

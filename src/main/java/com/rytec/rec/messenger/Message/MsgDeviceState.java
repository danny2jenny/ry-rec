package com.rytec.rec.messenger.Message;

import com.rytec.rec.device.DeviceStateBean;

/**
 * Created by danny on 17-1-23.
 * GIS Feature 的 状态消息
 */
public class MsgDeviceState {
    public int msg_no;
    public DeviceStateBean state = new DeviceStateBean();
}

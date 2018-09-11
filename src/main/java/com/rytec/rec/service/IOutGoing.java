package com.rytec.rec.service;

import com.rytec.rec.device.DeviceRuntimeBean;

/**
 * 向外发送数据的接口
 */
public interface IOutGoing {
    public void update(DeviceRuntimeBean deviceRuntimeBean);
}

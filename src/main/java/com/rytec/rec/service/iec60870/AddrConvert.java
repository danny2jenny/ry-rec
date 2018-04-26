package com.rytec.rec.service.iec60870;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.db.mapper.DeviceMapper;
import com.rytec.rec.db.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 光纤地址转换
 */
@Service
@Order(200)
public class AddrConvert extends RecBase implements ManageableInterface {

    @Autowired
    DeviceMapper deviceMapper;

    int addrMod = 4;

    // 地址转换的Map
    public HashMap<Integer, Integer> addrMap = new HashMap();

    @Override
    public void stop() {
        addrMap.clear();
    }

    @Override
    public void start() {
        stop();
        List<Device> devices = deviceMapper.selectByExample(null);

        int i = C_DeviceAddr.CRT_FIBER;          // 映射地址的初始值
        for (Device device : devices) {
            if ((device.getType() == 501) | (device.getType() == 502)) {
                addrMap.put(device.getId(), i);
                i += 4;
            }
        }
    }

    /**
     * 返回 光纤测温或者是电流传感的基础104地址
     *
     * @param deviceId
     * @return
     */
    public int getBase104Addr(int deviceId) {
        Integer rst;
        rst = addrMap.get(deviceId);
        if (rst != null) {
            return rst;
        } else {
            return -1;
        }
    }
}

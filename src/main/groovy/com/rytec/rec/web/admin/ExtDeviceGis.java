package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.rytec.rec.db.mapper.DeviceGisMapper;
import com.rytec.rec.db.model.DeviceGis;
import com.rytec.rec.db.model.DeviceGisExample;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by danny on 17-1-3.
 * <p>
 * 这是一个视图，只有查询列表
 */
public class ExtDeviceGis {

    @Autowired
    DeviceGisMapper deviceGisMapper;

    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<DeviceGis> list(ExtDirectStoreReadRequest request) {

        int deviceId = (Integer) request.getParams().getOrDefault("device", 0);

        if (deviceId == 0) {

            return deviceGisMapper.selectByExample(null);
        } else {
            DeviceGisExample deviceGisExample = new DeviceGisExample();
            deviceGisExample.createCriteria().andDidEqualTo(deviceId);
            return deviceGisMapper.selectByExample(deviceGisExample);
        }
    }
}

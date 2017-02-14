package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import com.rytec.rec.db.mapper.DeviceMapper;
import com.rytec.rec.db.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by danny on 16-12-21.
 * 设备管理
 */

@Controller
public class ExtDevice {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DeviceMapper deviceMapper;

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<Device> list() {
        return deviceMapper.selectByExample(null);

    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Device update(Device channel) {
        deviceMapper.updateByPrimaryKey(channel);
        return channel;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Device create(Device channel) {
        deviceMapper.insert(channel);
        return channel;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Device delete(Device channel) {
        deviceMapper.deleteByPrimaryKey(channel.getId());
        return channel;
    }
}

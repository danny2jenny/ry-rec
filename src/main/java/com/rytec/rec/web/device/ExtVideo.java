package com.rytec.rec.web.device;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import com.rytec.rec.db.mapper.ChannelMapper;
import com.rytec.rec.db.mapper.DeviceNodeMapper;
import com.rytec.rec.db.model.*;
import com.rytec.rec.util.ConstantDeviceFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by danny on 17-4-10.
 */

@Controller
public class ExtVideo {

    @Autowired
    ChannelMapper channelMapper;

    @Autowired
    DeviceNodeMapper deviceNodeMapper;

    // 得到NVR的列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<Channel> listNvr() {
        ChannelExample channelExample = new ChannelExample();
        channelExample.createCriteria().andTypeBetween(2000, 3000);
        return channelMapper.selectByExample(channelExample);
    }

    // 得到NVR配置的详细信息
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<DeviceNode> listNvrNode() {
        DeviceNodeExample deviceNodeExample = new DeviceNodeExample();
        deviceNodeExample.createCriteria().andDtypeEqualTo(401).andNfunEqualTo(ConstantDeviceFunction.DEV_FUN_PORT_MAIN);
        return deviceNodeMapper.selectByExample(deviceNodeExample);
    }

}

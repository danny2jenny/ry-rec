package com.rytec.rec.db;

import com.rytec.rec.db.mapper.ChannelNodeMapper;
import com.rytec.rec.db.mapper.DeviceNodeMapper;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.db.model.ChannelNodeExample;
import com.rytec.rec.db.model.DeviceNode;
import com.rytec.rec.db.model.DeviceNodeExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by danny on 16-12-28.
 * <p>
 * 数据库的所有配置信息都在这里集中读取和初始化
 */

@Service
public class DbConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<ChannelNode> channelNodeList;
    private List<DeviceNode> deviceNodeList;

    @Autowired
    ChannelNodeMapper channelNodeMapper;

    @Autowired
    DeviceNodeMapper deviceNodeMapper;

    private void initChannelNode() {

        ChannelNodeExample channelNodeExample = new ChannelNodeExample();

        channelNodeExample.createCriteria().andNidGreaterThan(0);

        channelNodeList = channelNodeMapper.selectByExample(channelNodeExample);

    }

    private void initDeviceNode() {

        DeviceNodeExample deviceNodeExample = new DeviceNodeExample();

        deviceNodeExample.createCriteria().andNidGreaterThan(0);

        deviceNodeList = deviceNodeMapper.selectByExample(deviceNodeExample);
    }

    // Channel 和 Node 的对应关系
    public List<ChannelNode> getChannelNodeList() {
        if (channelNodeList == null) {
            initChannelNode();
        }
        return channelNodeList;
    }

    //Node 和 Device 的对应关系
    public List<DeviceNode> getDeviceNodeList() {
        if (deviceNodeList == null) {
            initDeviceNode();
        }
        return deviceNodeList;
    }

}

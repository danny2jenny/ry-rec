package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.rytec.rec.db.mapper.ChannelMapper;
import com.rytec.rec.db.model.Channel;
import com.rytec.rec.db.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by danny on 16-12-20.
 * <p>
 * Channel 的 Ext 对象
 */

@Controller
public class ExtChannel {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ChannelMapper channelMapper;

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<Channel> list() {
        return channelMapper.selectByExample(null);

    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Channel update(Channel channel) {
        channelMapper.updateByPrimaryKey(channel);
        return channel;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Channel create(Channel channel) {
        channelMapper.insert(channel);
        return channel;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Channel delete(Channel channel) {
        channelMapper.deleteByPrimaryKey(channel.getId());
        return channel;
    }

}

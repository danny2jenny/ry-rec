package com.rytec.rec.web.device;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import com.rytec.rec.db.mapper.ChannelMapper;
import com.rytec.rec.db.model.Channel;
import com.rytec.rec.db.model.ChannelExample;
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

    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<Channel> listNvr() {
        ChannelExample channelExample = new ChannelExample();
        channelExample.createCriteria().andTypeBetween(2000, 3000);
        return channelMapper.selectByExample(channelExample);
    }

}

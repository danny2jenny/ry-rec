package com.rytec.rec.channel;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-12-16.
 * 所有的Channel的管理
 */

@Service
public class ChannelManager {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public void channelOnState(String cid, int state) {
        logger.debug("通道信息：" + cid + '-' + state);
    }
}

package com.rytec.rec.channel.BaseChannel;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by danny on 17-4-1.
 */
public class VideoBaseChannel implements ChannelInterface {
    @Autowired
    VideoService videoService;

    @Override
    public int sendMsg(Object msg) {
        return videoService.ptzControl(msg);
    }

    /**
     * 在VideoService中处理了，这里不用处理
     *
     * @param channelId Integer
     * @param online
     */
    public void channelOnline(Object channelId, boolean online) {

    }
}

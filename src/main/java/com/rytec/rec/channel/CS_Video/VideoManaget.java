package com.rytec.rec.channel.CS_Video;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.util.AnnotationChannelType;
import com.rytec.rec.util.AnnotationJSExport;
import org.springframework.stereotype.Service;

/**
 * Created by Danny on 2017/2/22.
 * 超视流媒体服务器的接口
 * <p>
 * 节点功能：
 * 1、告警
 * 2、PTZ调用
 * 3、PTZ设置
 */

@Service
@AnnotationChannelType(2001)
@AnnotationJSExport("超视流媒体服务器")
public class VideoManaget implements ChannelInterface {

    public int sendMsg(ChannelMessage msg) {
        return 0;
    }
}

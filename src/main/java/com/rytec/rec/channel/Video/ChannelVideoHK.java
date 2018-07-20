package com.rytec.rec.channel.Video;

import com.rytec.rec.channel.Video.BaseChannel.ChannelVideoBase;
import com.rytec.rec.util.AnnotationChannelType;
import com.rytec.rec.util.AnnotationJSExport;
import org.springframework.stereotype.Service;

/**
 * Created by Danny on 2017/3/13.
 */
@Service
@AnnotationChannelType(2001)
@AnnotationJSExport("海康 DVR")
public class ChannelVideoHK extends ChannelVideoBase {

}

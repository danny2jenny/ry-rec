package com.rytec.rec.channel.Video;

import com.rytec.rec.channel.Video.BaseChannel.ChannelVideoBase;
import com.rytec.rec.util.AnnotationChannelType;
import com.rytec.rec.util.AnnotationJSExport;
import org.springframework.stereotype.Service;

@Service
@AnnotationChannelType(2002)
@AnnotationJSExport("大华 DVR")
public class ChannelVideoDh extends ChannelVideoBase {
}

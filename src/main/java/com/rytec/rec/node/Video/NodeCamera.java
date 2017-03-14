package com.rytec.rec.node.Video;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.node.NodeConfig;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import org.springframework.stereotype.Service;

/**
 * Created by Danny on 2017/3/13.
 */

@Service
@AnnotationNodeType(4001)
@AnnotationJSExport("摄像机")
public class NodeCamera extends Deserializers.Base implements NodeInterface {

    // 命令编码
    public ChannelMessage genMessage(int where, int nodeId, int cmd, int value) {
        return null;
    }

    //消息解码
    public void decodeMessage(ChannelMessage msg) {

    }

    // 发送云台控制
    public int sendMessage(NodeMessage nodeMsg) {
        /*
        通过 node 查询到 channelId、 nodeId、Adr，然后去操作PTZ接口
         */
        return 0;
    }

    public boolean valueCompare(NodeConfig cfg, Object oldVal, Object newVal) {
        return true;
    }

}

package com.rytec.rec.node.video;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.*;
import com.rytec.rec.node.modbus.base.BaseModbusNode;
import com.rytec.rec.node.modbus.base.ModbusNodeInterface;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Danny on 2017/3/13.
 */

@Service
@AnnotationNodeType(4001)
@AnnotationJSExport("摄像机")
public class NodeCamera extends BaseModbusNode implements ModbusNodeInterface {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ChannelManager channelManager;

    @Autowired
    NodeManager nodeManager;

    // 命令编码
    @Override
    public Object genMessage(int where, int nodeId, int cmd, int regCount, int value) {
        return null;
    }

    //消息解码
    public void decodeMessage(Object msg) {

    }

    // 发送云台控制
    public int sendMessage(NodeMessage msg) {
        /*
        通过 node 查询到 channelId、 nodeId、Adr，然后去操作PTZ接口
         */
        ChannelNode channelNode = nodeManager.getChannelNodeByNodeId(msg.node).channelNode;
        ChannelInterface channel = channelManager.getChannelInterface(channelNode.getCtype());
        channel.sendMsg(msg);
        return 0;
    }

    public boolean needUpdate(NodeConfig cfg, java.lang.Object oldVal, java.lang.Object newVal) {
        return true;
    }

    /**
     * 通讯健康度
     *
     * @param h
     */
    public void goodHelth(Object msg, boolean h) {

    }

    /**
     * @return
     */
    public Object getCfg() {
        return null;
    }

    ;

}

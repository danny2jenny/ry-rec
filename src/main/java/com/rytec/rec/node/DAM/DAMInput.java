package com.rytec.rec.node.DAM;

import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.node.*;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.AnnotationNodeType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-12-14.
 * <p>
 * 命令格式：
 * byte     modbus地址
 * byes     0x02 命令
 * word     端口地址，从0开始
 * word     数量：1
 * word     CRC16 (不生成)
 * <p>
 * 返回格式：
 * byte     modbus地址
 * byes     0x02 命令
 * byte     字节数
 * byte     端口值
 * word     CRC16 (不生成)
 */

@Service
@AnnotationNodeType(1002)
@AnnotationJSExport("DMA 输入")
public class DAMInput extends DAM_BASE {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    // 命令编码
    public ChannelMessage genMessage(int where, int nodeId, int cmd, int value) {

        NodeRuntimeBean nodeRuntimeBean = nodeManager.getChannelNodeByNodeId(nodeId);

        ChannelMessage frame = new ChannelMessage();

        frame.from = where;
        frame.nodeId = nodeId;
        frame.type = cmd;
        frame.responseLen = 6;

        ByteBuf buf = Unpooled.buffer(6);
        buf.writeByte(nodeRuntimeBean.channelNode.getAdr());
        buf.writeByte(0x02);
        buf.writeShort(0x00);
        buf.writeShort(0x08);

        frame.payload = buf;

        return frame;
    }

    // 回应解码

}



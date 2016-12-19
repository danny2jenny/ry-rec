package com.rytec.rec.node.DAM;

import com.rytec.rec.bean.ChannelNode;
import com.rytec.rec.channel.ModbusTcpServer.ModbusMessage;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeProtocolInterface;
import com.rytec.rec.util.CommandType;
import com.rytec.rec.util.NodeType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by danny on 16-12-13.
 * <p>
 * 北京聚英电子相关的Modbus控制器
 * <p>
 * <p>
 * 写命令格式：
 * byte     modbus地址
 * byes     0x05 命令
 * word     端口地址，从0开始
 * word     开：FF 00, 关 00 00
 * word     CRC16 (不生成)
 * <p>
 * 返回格式：
 * byte     modbus地址
 * byes     0x05 命令
 * word     端口地址，从0开始
 * word     开：FF 00, 关 00 00
 * word     CRC16 (不生成)
 * <p>
 * -----------------------------------
 * 读命令：
 * byte     modbus地址
 * byes     0x01 命令
 * word     端口地址，从0开始
 * word     数量 1
 * word     CRC16 (不生成)
 * <p>
 * 返回格式：
 * byte     modbus地址
 * byes     0x01 命令
 * byte     字节数 1
 * byte     开：FF , 关 00
 * word     CRC16 (不生成)
 */

@Service
@NodeType(1001)
public class DamOutput implements NodeProtocolInterface {

    @Autowired
    NodeManager nodeManager;

    /*
     */
    public ModbusMessage genMessage(int where, int nodeId, int cmd, int value) {

        ChannelNode channelNode = nodeManager.getChannelNodeByNodeId(nodeId);

        ModbusMessage frame = new ModbusMessage();
        frame.from = where;
        frame.nodeId = nodeId;

        frame.type = cmd;

        ByteBuf buf;
        switch (cmd) {
            // 写入
            case CommandType.MODBUS_CMD_WRITE:
                buf = Unpooled.buffer(6);
                frame.responseLen = 8;
                buf.writeByte(channelNode.add);     //地址
                buf.writeByte(0x05);                //命令
                buf.writeShort(channelNode.no);     //寄存器
                buf.writeShort(value);
                frame.payload = buf.array();
                break;

            //状态查询
            case CommandType.MODBUS_CMD_READ:
                buf = Unpooled.buffer(6);
                frame.responseLen = 6;
                buf.writeByte(channelNode.add);
                buf.writeByte(0x01);
                buf.writeShort(channelNode.no);     //地址
                buf.writeShort(1);                  //查询数量
                frame.payload = buf.array();
                break;
        }

        return frame;
    }

    //消息解码
    public int decodeMessage(Object msg) {

        int value = 0;
        ModbusMessage respond = (ModbusMessage) msg;
        switch (respond.type) {
            case CommandType.MODBUS_CMD_READ:
                value = respond.payload[3];
                break;
            case CommandType.MODBUS_CMD_WRITE:
                value = respond.payload[4];
                break;
        }
        return value;
    }
}

package com.rytec.rec.node.DAM;

import com.rytec.rec.channel.ModbusTcpServer.ModbusMessage;
import com.rytec.rec.node.AbstractNode;
import com.rytec.rec.util.CommandType;
import com.rytec.rec.util.NodeType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Service;


/**
 * Created by danny on 16-12-13.
 * <p>
 * 北京聚英电子相关的Modbus控制器
 */

@Service
@NodeType(1001)
public class NodeSwitch implements AbstractNode {
    /*
    * add 地址
    * type 类型 100 关闭， 101开启，200 状态查询
     */
    public ModbusMessage genFrame(int where, int add, int reg, int cmd) {

        ModbusMessage frame = new ModbusMessage();
        frame.from = where;
        frame.add = add;
        frame.no = reg;
        frame.type = cmd;

        ByteBuf buf;
        switch (cmd) {
            //关闭
            case CommandType.CMD_OFF:
                buf = Unpooled.buffer(6);
                frame.responseLen = 8;
                buf.writeByte(add);
                buf.writeByte(0x05);
                buf.writeShort(reg);
                buf.writeByte(0x00);
                buf.writeByte(0x00);
                frame.payload = buf.array();
                break;

            //开启
            case CommandType.CMD_ON:
                buf = Unpooled.buffer(6);
                frame.responseLen = 8;
                buf.writeByte(add);
                buf.writeByte(0x05);
                buf.writeShort(reg);
                buf.writeByte(0xFF);
                buf.writeByte(0x00);
                frame.payload = buf.array();
                break;

            //状态查询
            case CommandType.CMD_QUERY:
                buf = Unpooled.buffer(6);
                frame.responseLen = 6;
                buf.writeByte(add);
                buf.writeByte(0x01);
                buf.writeShort(reg);    //地址
                buf.writeShort(1);      //查询数量
                frame.payload = buf.array();
                break;
        }

        return frame;
    }
}

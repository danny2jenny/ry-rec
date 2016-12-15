package com.rytec.rec.node.DAM;

import com.rytec.rec.channel.ModbusTcpServer.ModbusFrame;
import com.rytec.rec.node.AbstractNode;
import com.rytec.rec.util.NodeType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-12-14.
 */

@Service
@NodeType(1002)
public class Input implements AbstractNode {

    public ModbusFrame genFrame(int where, int add, int reg, int cmd) {
        ModbusFrame frame = new ModbusFrame();

        frame.from = where;
        frame.add = add;
        frame.no = reg;

        ByteBuf buf = Unpooled.buffer(6);
        buf.writeByte(add);
        buf.writeByte(0x02);
        buf.writeShort(reg);
        buf.writeShort(0x01);

        return frame;
    }
}

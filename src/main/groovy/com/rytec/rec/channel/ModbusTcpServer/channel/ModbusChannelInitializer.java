package com.rytec.rec.channel.ModbusTcpServer.channel;


import com.rytec.rec.channel.ModbusTcpServer.ModbusConstants;
import com.rytec.rec.channel.ModbusTcpServer.entity.ModbusFrame;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by danny on 16-11-22.
 */
public class ModbusChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SimpleChannelInboundHandler handler;

    public ModbusChannelInitializer(ModbusRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        /*
         * Modbus TCP Frame Description
         *  - max. 260 Byte (ADU = 7 Byte MBAP + 253 Byte PDU)
         *  - Length field includes Unit Identifier + PDU
         *
         * <----------------------------------------------- ADU -------------------------------------------------------->
         * <---------------------------- MBAP -----------------------------------------><------------- PDU ------------->
         * +------------------------+---------------------+----------+-----------------++---------------+---------------+
         * | Transaction Identifier | Protocol Identifier | Length   | Unit Identifier || Function Code | Data          |
         * | (2 Byte)               | (2 Byte)            | (2 Byte) | (1 Byte)        || (1 Byte)      | (1 - 252 Byte |
         * +------------------------+---------------------+----------+-----------------++---------------+---------------+
         */

        // 基于长度过滤的解析器
        pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(ModbusConstants.ADU_MAX_LENGTH, 4, 2));

        //Modbus encoder, decoder
        pipeline.addLast("encoder", new ModbusEncoder());
        pipeline.addLast("decoder", new ModbusDecoder(handler instanceof ModbusRequestHandler));

        if (handler instanceof ModbusRequestHandler) {
            //server
            pipeline.addLast("requestHandler", handler);
        } else if (handler instanceof ModbusResponseHandler) {
            //async client
            pipeline.addLast("responseHandler", handler);
        } else {
            //sync client
            pipeline.addLast("responseHandler", new ModbusResponseHandler() {

                @Override
                public void newResponse(ModbusFrame frame) {
                    //discard in sync mode
                }
            });
        }
    }
}


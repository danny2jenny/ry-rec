package com.rytec.rec.channel.ModbusTcpClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncRunner {
    @Autowired
    ChannelModbusMaster channelModbusMaster;

    @Async
    public void runLoop() {
        while (channelModbusMaster.inLoop) {
            if (channelModbusMaster.modbusClients.isEmpty()){
                channelModbusMaster.inLoop = false;
            }
            for (RecModbusMasterSession session : channelModbusMaster.modbusClients.values()) {
                session.startCom();
            }
        }
    }
}

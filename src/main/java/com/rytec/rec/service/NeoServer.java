package com.rytec.rec.service;

import org.eclipse.neoscada.protocol.iec60870.ProtocolOptions;
import org.eclipse.neoscada.protocol.iec60870.ProtocolOptions.Builder;
import org.eclipse.neoscada.protocol.iec60870.server.Server;
import org.eclipse.neoscada.protocol.iec60870.server.ServerModule;
import org.eclipse.neoscada.protocol.iec60870.server.data.DataModule;
import org.eclipse.neoscada.protocol.iec60870.server.data.DataModuleOptions;
import org.eclipse.neoscada.protocol.iec60870.server.data.testing.SineDataModel;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

public class NeoServer {
    Server server;

    @PostConstruct
    public void start() {
        final List<ServerModule> modules = new LinkedList<>();

        final Builder builder = new ProtocolOptions.Builder();

        final DataModuleOptions dataModuleOptions = new DataModuleOptions.Builder().build();

        builder.setMaxUnacknowledged((short) 10);
        builder.setAcknowledgeWindow((short) (builder.getMaxUnacknowledged() * 0.66));

        final NeoServerModel dataModel = new NeoServerModel();
        modules.add(new DataModule(dataModuleOptions, dataModel));

        server = new Server((short) 2405, builder.build(), modules);
    }

    public void stop() throws Exception {
        server.close();
    }
}

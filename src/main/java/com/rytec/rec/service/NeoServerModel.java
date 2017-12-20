package com.rytec.rec.service;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import org.eclipse.neoscada.protocol.iec60870.asdu.ASDUHeader;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.ASDUAddress;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.InformationObjectAddress;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.QualityInformation;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.Value;
import org.eclipse.neoscada.protocol.iec60870.io.MirrorCommand;
import org.eclipse.neoscada.protocol.iec60870.server.data.AbstractBaseDataModel;
import org.eclipse.neoscada.protocol.iec60870.server.data.BackgroundIterator;
import org.eclipse.neoscada.protocol.iec60870.server.data.DataListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class NeoServerModel extends AbstractBaseDataModel {

    private final int size;
    private final List<Value<Float>> values;

    private static final ASDUAddress ASDU_ADDRESS = ASDUAddress.valueOf ( 1 );


    protected synchronized Value<?> performRead ( final ASDUAddress asduAddress, final InformationObjectAddress address )
    {
        return this.values.get ( address.getAddress () );
    }


    public NeoServerModel() {
        super("SineDataModel");
        this.size = 10;
        this.values = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.values.add(new Value<>(0.0f, System.currentTimeMillis(), QualityInformation.INVALID));
        }
    }

    @Override
    public ListenableFuture<Value<?>> read(ASDUAddress asduAddress, InformationObjectAddress address) {
        if ( ASDU_ADDRESS.equals ( asduAddress.getAddress () ) )
        {
            return null;
        }

        return this.executor.submit ( new Callable<Value<?>>() {

            @Override
            public Value<?> call () throws Exception
            {
                return performRead ( asduAddress, address );
            }
        } );
    }

    @Override
    public ListenableFuture<Void> readAll(ASDUAddress asduAddress, Runnable prepare, DataListener listener) {
        return null;
    }

    @Override
    public BackgroundIterator createBackgroundIterator() {
        return null;
    }

    @Override
    public void forAllAsdu(Consumer<ASDUAddress> function, Runnable ifNoneFound) {

    }

    @Override
    public void forAllAsdu(Function<ASDUAddress, Void> function, Runnable ifNoneFound) {

    }

    @Override
    public void writeCommand(ASDUHeader header, InformationObjectAddress informationObjectAddress, boolean state, byte type, MirrorCommand mirrorCommand, boolean execute) {

    }

    @Override
    public void writeFloatValue(ASDUHeader header, InformationObjectAddress informationObjectAddress, float value, byte type, MirrorCommand mirrorCommand, boolean execute) {

    }

    @Override
    public void writeScaledValue(ASDUHeader header, InformationObjectAddress informationObjectAddress, short value, byte type, MirrorCommand mirrorCommand, boolean execute) {

    }
}

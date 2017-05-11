package com.rytec.rec.device.operator;

import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.util.AnnotationDeviceType;
import com.rytec.rec.util.AnnotationJSExport;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 17-5-10.
 *
 *  全景设备
 */

@Service
@AnnotationDeviceType(9999)
@AnnotationJSExport("全景")
public class Panorama extends AbstractOperator {

    @Override
    public int operate(int from, int device, int act, Object parm) {
        return 1;
    }
}

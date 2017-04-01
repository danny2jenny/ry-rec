package com.rytec.rec.device.operator;

import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.AnnotationDeviceType;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantDeviceFunction;
import org.springframework.stereotype.Service;

/**
 * Created by Danny on 2017/3/13.
 *
 * 摄像机设备
 * 动作：PTZ
 *
 */
@Service
@AnnotationDeviceType(401)
@AnnotationJSExport("摄像头")
public class Camera extends AbstractOperator {

    /**
     * 动作行常量
     */

    @AnnotationJSExport("PTZ调用")
    public static int ACT_PTZ = 100;        //PTZ

    @Override
    public int operate(int from, int device, int act, Object parm) {
        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = from;
        nodeMsg.type = ConstantCommandType.GENERAL_WRITE;
        nodeMsg.value = parm;

        return setValue(device, ConstantDeviceFunction.DEV_FUN_PORT_MAIN, nodeMsg);
    }
}

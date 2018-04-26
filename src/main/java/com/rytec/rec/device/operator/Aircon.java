package com.rytec.rec.device.operator;


import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.device.state.StateAircon;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.*;
import org.springframework.stereotype.Service;


/**
 * Created by danny on 16-11-29.
 * 空调
 * 一个端口，
 */
@Service
@AnnotationDeviceType(301)
@AnnotationJSExport("空调")
public class Aircon extends AbstractOperator {


    /**
     * 动作常量
     */
    @AnnotationJSExport("关闭")
    public static int ACT_OFF = 1;          //关闭

    @AnnotationJSExport("开启制冷")
    public static int ACT_COLD = 2;         //开启制冷

    @AnnotationJSExport("开启制热")
    public static int ACT_HOT = 3;          //开启制热


    /**
     * 生成状态对象
     *
     * @return
     */
    @Override
    public Object generateStateBean() {
        return new StateAircon();
    }

    /**
     * 状态改变
     *
     * @param deviceId
     * @param fun
     * @param oldValue
     * @param newValue
     * @param unit
     */
    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue, String unit) {

        Short val = (Short) newValue;

        // 得到运行状态
        DeviceRuntimeBean deviceRuntimeBean = deviceManager.getDeviceRuntimeList().get(deviceId);
        StateAircon stateAircon = (StateAircon)deviceRuntimeBean.runtime.state;
        stateAircon.value = val.intValue();

        // 处理掉线
        if (newValue == null) {
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_OFFLINE;
            // 向客户端广播消息
            clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
            return;
        }



        switch (val) {
            case ConstantAircon.STATE_STOP:
                deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_OFF;
                break;
            case ConstantAircon.STATE_COLD:
                deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ON;
                break;
            case ConstantAircon.STATE_HOT:
                deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ON;
                break;
        }

        // 向客户端广播消息
        clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
    }

    @Override
    public int operate(int from, int device, int act, Object parm) {

        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = from;
        nodeMsg.type = ConstantCommandType.GENERAL_WRITE;

        switch (act) {
            case 1:
                // 关闭
                nodeMsg.value = ConstantAircon.STATE_STOP;
                break;
            case 2:
                // 开启制冷
                nodeMsg.value = ConstantAircon.STATE_COLD;
                break;
            case 3:
                // 开启制热
                nodeMsg.value = ConstantAircon.STATE_HOT;
                break;
            default:
                return ConstantErrorCode.DEVICE_ACT_NOT_EXIST;
        }

        return this.setValue(device, ConstantDeviceFunction.DEV_FUN_PORT_A, nodeMsg);
    }

}

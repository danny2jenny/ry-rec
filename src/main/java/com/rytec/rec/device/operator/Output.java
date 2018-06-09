package com.rytec.rec.device.operator;


import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.device.state.StateOutput;
import com.rytec.rec.util.ConstantMessageType;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.*;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-11-29.
 * 输出
 * 三个端口：
 * 101:control：开启、关闭
 * 102:feedback：辅助节点，判断开启、关闭是否成功
 * 103:remote：本地、远程
 * <p>
 * 信号  注意!!!!!!信号和动作不能是0
 * 1、本地远程切换-远程
 * 2、本地远程切换-本地
 * 3、远程状态改变-开
 * 4、远程状态改变-关
 */
@Service
@AnnotationDeviceType(101)
@AnnotationJSExport("远程开关")
public class Output extends AbstractOperator {

    /**
     * 常量的列表，表示该设备可以输出那些信号
     */
    //@AnnotationJSExport("开启")
    public static int SIG_ON = 1;           //开启信号1

    //@AnnotationJSExport("关闭")
    public static int SIG_OFF = -1;          //关闭信号0

    //@AnnotationJSExport("就地")
    public static int SIG_LOCAL = 11;       //就地

    //@AnnotationJSExport("远程")
    public static int SIG_REMOTE = 12;      //远程

    /**
     * 动作常量
     */

    @AnnotationJSExport("关闭")
    public static int ACT_OFF = 100;        //关闭
    @AnnotationJSExport("开启")
    public static int ACT_ON = 101;         //开启

    /**
     * 开关控制
     *
     * @param deviceId
     * @param from
     * @param value
     */
    public int setSwitch(int deviceId, int from, Boolean value) {
        NodeMessage nodeMsg = new NodeMessage();
        nodeMsg.from = from;
        nodeMsg.type = ConstantCommandType.GENERAL_WRITE;
        nodeMsg.value = value;
        return this.setValue(deviceId, ConstantDeviceFunction.DEV_FUN_PORT_A, nodeMsg);
    }

    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue, String unit) {

        // 得到运行状态
        DeviceRuntimeBean deviceRuntimeBean = deviceManager.getDeviceRuntimeList().get(deviceId);

        // Output 的State对象
        StateOutput state = (StateOutput) deviceRuntimeBean.runtime.state;

        // 处理掉线
        // 处理掉线
        if (newValue == null) {

            switch (fun) {
                case ConstantDeviceFunction.DEV_FUN_PORT_A:
                    deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_OFFLINE;
                    state.output = null;
                    break;
                case ConstantDeviceFunction.DEV_FUN_PORT_C:
                    state.remote = null;
                    break;
                case ConstantDeviceFunction.DEV_FUN_PORT_B:
                    state.feedback = null;
                    break;
            }
            clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);
            return;
        }

        // 根据fun对状态进行更新
        switch (fun) {
            case ConstantDeviceFunction.DEV_FUN_PORT_A:
                // 开关状态
                if ((Boolean) newValue == true) {
                    deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_ON;
                    state.output = ConstantDeviceState.STATE_ON;
                } else {
                    deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_OFF;
                    state.output = ConstantDeviceState.STATE_OFF;
                }
                break;
            case ConstantDeviceFunction.DEV_FUN_PORT_C:
                // 远程就地状态
                if ((Boolean) newValue == true) {
                    state.remote = ConstantDeviceState.STATE_ON;
                } else {
                    state.remote = ConstantDeviceState.STATE_OFF;
                }
                break;
            case ConstantDeviceFunction.DEV_FUN_PORT_B:
                // 反馈状态
                if ((Boolean) newValue == true) {
                    state.feedback = ConstantDeviceState.STATE_ON;
                } else {
                    state.feedback = ConstantDeviceState.STATE_OFF;
                }
                break;
            case ConstantDeviceFunction.DEV_FUN_PORT_D:
                // 本地开关
                if ((Boolean) newValue == true) {
                    operate(ConstantFromWhere.FROM_SYSTEM, deviceRuntimeBean.device.getId(), Output.ACT_ON, null);
                } else {
                    operate(ConstantFromWhere.FROM_SYSTEM, deviceRuntimeBean.device.getId(), Output.ACT_OFF, null);
                }
                break;
        }

        // 向客户端广播消息
        clientBroadcast(ConstantMessageType.DEVICE_STATE, deviceRuntimeBean);

    }

    @Override
    public int operate(int from, int device, int act, Object parm) {
        switch (act) {
            case 100:
                setSwitch(device, from, false);
                break;
            case 101:
                setSwitch(device, from, true);
                break;
            default:
                return ConstantErrorCode.DEVICE_ACT_NOT_EXIST;
        }

        return 0;
    }

    @Override
    public Object generateStateBean() {
        return new StateOutput();
    }

}

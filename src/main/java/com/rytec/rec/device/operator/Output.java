package com.rytec.rec.device.operator;


import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.*;
import org.slf4j.LoggerFactory;
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
     * 动作行常量
     */

    @AnnotationJSExport("关闭")
    public static int ACT_OFF = 100;        //关闭
    @AnnotationJSExport("开启")
    public static int ACT_ON = 101;         //开启


    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

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
        return this.setValue(deviceId, ConstantDeviceFunction.DEV_FUN_PORT_MAIN, nodeMsg);
    }

    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue) {

        if ((Boolean) newValue == true) {
            setState(deviceId, ConstantDeviceState.STATE_ON);
        } else {
            setState(deviceId, ConstantDeviceState.STATE_OFF);
        }

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
        return new OutputState();
    }

}

package com.rytec.rec.device.operator;

import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.util.ConstantDeviceState;
import com.rytec.rec.util.AnnotationDeviceType;
import com.rytec.rec.util.AnnotationJSExport;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-11-29.
 * 模拟量
 * 一个端口只读
 * <p>
 * 信号：
 * 1、超限（A）
 * 2、超限（B）
 * 3、超限（C）
 */
@Service
@AnnotationDeviceType(201)
@AnnotationJSExport("模拟输入")
public class Analog extends AbstractOperator {

    @AnnotationJSExport("+高限告警")
    public static int SIG_HIGH_2 = 20;

    @AnnotationJSExport("+高限联动")
    public static int SIG_HIGH_1 = 10;

    @AnnotationJSExport("o正常")
    public static int SIG_NORMAL = 1;

    @AnnotationJSExport("-低限联动")
    public static int SIG_LOW_1 = -10;

    @AnnotationJSExport("-低限告警")
    public static int SIG_LOW_2 = -20;

    @Override
    public void onValueChanged(int deviceId, int fun, Object oldValue, Object newValue) {
        setState(deviceId, ConstantDeviceState.STATE_ON);
    }
}

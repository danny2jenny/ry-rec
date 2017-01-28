package com.rytec.rec.device.operator;

import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.util.DeviceType;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-11-29.
 * 模拟量
 * 一个端口只读
 */
@Service
@DeviceType(201)
public class Analog extends AbstractOperator {
}

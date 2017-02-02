package com.rytec.rec.device.operator;


import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.util.Description;
import com.rytec.rec.util.DeviceType;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * Created by danny on 16-11-29.
 * 空调
 * 一个端口，
 */
@Service
@DeviceType(301)
@Description("空调")
public class Aircon extends AbstractOperator {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

}

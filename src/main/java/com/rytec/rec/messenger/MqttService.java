package com.rytec.rec.messenger;

import org.slf4j.LoggerFactory;

/**
 * Created by Danny on 2017/3/17.
 */
public class MqttService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public void startCase(String message) {
        logger.debug(message);
    }
}

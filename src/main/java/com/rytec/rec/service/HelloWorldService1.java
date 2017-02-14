package com.rytec.rec.service;

/**
 * Created by danny on 16-11-4.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("1")
public class HelloWorldService1 extends HelloWorldService{

    private final Logger logger = LoggerFactory.getLogger(HelloWorldService1.class);

    public String getDesc() {

        return "1";

    }


}
package com.rytec.rec.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-11-18.
 */

@Service
@Qualifier("2")
public class HelloWorldService2 extends HelloWorldService {

    public String getDesc() {

        return "2";

    }

}

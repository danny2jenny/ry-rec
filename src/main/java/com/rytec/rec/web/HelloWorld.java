package com.rytec.rec.web;

/**
 * Created by danny on 16-11-4.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorld {

    private final Logger logger = LoggerFactory.getLogger(HelloWorld.class);

    @RequestMapping(value = "/test")
    @ResponseBody
    public String ResponseTest() {
        return "";
    }
}
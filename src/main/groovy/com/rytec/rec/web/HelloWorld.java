package com.rytec.rec.web;

/**
 * Created by danny on 16-11-4.
 */

import com.rytec.rec.db.DbConfig;
import com.rytec.rec.service.HelloWorldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
package com.rytec.rec.web.test;

import com.rytec.rec.service.heshen.HeShen80870Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by danny on 17-6-15.
 */

@Controller
public class test {
    @Autowired
    HeShen80870Service heShen80870Service;

    @GetMapping("/test")
    @ResponseBody
    public String system() {
        //heShen80870Service.uploadCfg();
        return "OK";
    }
}

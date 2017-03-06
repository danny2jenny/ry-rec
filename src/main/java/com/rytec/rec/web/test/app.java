package com.rytec.rec.web.test;

import com.rytec.rec.app.AppManager;
import com.rytec.rec.messenger.Message.WebMessage;
import com.rytec.rec.messenger.WebPush;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Danny on 2017/3/5.
 */

@Controller
public class app {

    @Autowired
    AppManager appManager;

    @Autowired
    WebPush webPush;

    @GetMapping("/system")
    @ResponseBody
    public void system() {
        appManager.systemReload();
    }

    @RequestMapping("msg")
    @ResponseBody
    public String msg() {
        webPush.clientBroadcast(new WebMessage());
        return "OK";
    }

}

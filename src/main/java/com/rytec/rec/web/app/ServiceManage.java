package com.rytec.rec.web.app;

import com.rytec.rec.app.AppManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by Danny on 2017/3/5.
 */

@Controller
public class ServiceManage {

    @Autowired
    AppManager appManager;

    @GetMapping("/system")
    @ResponseBody
    public void system() {
        appManager.systemReload();
    }

}

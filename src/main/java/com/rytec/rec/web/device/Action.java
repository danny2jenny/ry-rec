package com.rytec.rec.web.device;

import com.rytec.rec.device.operator.Output;
import com.rytec.rec.util.ConstantFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by danny on 17-1-16.
 */
@Controller
public class Action {

    @Autowired
    Output output;

    @GetMapping("/switch/{device}/{sw}")
    @ResponseBody
    public void getFeaturesByLayer(@PathVariable int device, @PathVariable int sw) {
        if (sw > 0) {
            output.setSwitch(device, ConstantFromWhere.FROM_USER, true);
        } else {
            output.setSwitch(device, ConstantFromWhere.FROM_USER, false);
        }

    }
}

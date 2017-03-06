package com.rytec.rec.web.device;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.util.ConstantFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by danny on 17-1-16.
 */
@Controller
public class Action {

    @Autowired
    DeviceManager deviceManager;

    @ExtDirectMethod
    private void operate(int device, int act, Object parm) {
        AbstractOperator operator = deviceManager.getOperatorByDeviceId(device);
        if (operator != null) {
            operator.operate(ConstantFromWhere.FROM_USER, device, act, parm);
        }
    }


}

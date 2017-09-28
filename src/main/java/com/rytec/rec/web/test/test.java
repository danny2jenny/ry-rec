package com.rytec.rec.web.test;

import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantFromWhere;
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
    NodeManager nodeManager;

    @GetMapping("/test")
    @ResponseBody
    public void system() {
        NodeMessage nodeMessage = new NodeMessage();
        nodeMessage.node = 7;
        nodeMessage.type = ConstantCommandType.GENERAL_READ;
        nodeMessage.from = ConstantFromWhere.FROM_USER;
        nodeMessage.value = true;
        nodeManager.onMessage(nodeMessage);

        nodeMessage.node = 8;
        nodeManager.onMessage(nodeMessage);

        nodeMessage.node = 13;
        nodeManager.onMessage(nodeMessage);
    }
}

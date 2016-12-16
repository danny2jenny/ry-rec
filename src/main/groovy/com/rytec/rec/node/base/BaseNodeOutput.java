package com.rytec.rec.node.base;

import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-12-16.
 * 基本节点：开关控制
 */
@Service
public class BaseNodeOutput {

    //得到节点状态
    int getState(int id) {
        return 0;
    }

    //设置节点状态
    void setState(int id, int value) {

    }
}

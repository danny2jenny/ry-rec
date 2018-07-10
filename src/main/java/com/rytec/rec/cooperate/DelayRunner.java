package com.rytec.rec.cooperate;

import com.rytec.rec.app.RecBase;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 延时任务的运行对象
 */
@Service
public class DelayRunner extends RecBase {
    @Autowired
    CooperateManager cooperateManager;

    @Autowired
    NodeManager nodeManager;

    @Async
    public void doDelayTask() {

        DelayTask task;
        Object data;

        while (true) {
            try {
                task = cooperateManager.delayQueue.take();
                data = task.taskData;

                // 任务类型是NodeMessage
                if (data instanceof NodeMessage) {
                    nodeManager.sendMsg((NodeMessage) data);
                }

            } catch (InterruptedException e) {

            }
        }

    }
}

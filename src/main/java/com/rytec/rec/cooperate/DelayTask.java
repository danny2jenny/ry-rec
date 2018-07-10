package com.rytec.rec.cooperate;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时对象
 */
public class DelayTask implements Delayed {

    Object taskData;
    long startTime;

    /**
     * 构造函数
     *
     * @param data  任务数据
     * @param delay 任务延时
     */
    public DelayTask(Object data, long delay) {
        this.taskData = data;
        this.startTime = System.currentTimeMillis() + delay;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long td = this.getDelay(TimeUnit.MILLISECONDS);
        long od = o.getDelay(TimeUnit.MILLISECONDS);
        return td > od ? 1 : td == od ? 0 : -1;
    }
}

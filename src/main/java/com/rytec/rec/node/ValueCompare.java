package com.rytec.rec.node;

/**
 * Created by danny on 17-5-17.
 * <p>
 * 返回True 需要更新，返回False 不需要更新
 */
public class ValueCompare {

    public static boolean booleanNeedUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        boolean rst = false;
        if (oldVal != newVal) {
            rst = true;
        }
        return rst;
    }

    public static boolean analogNeedUpdate(NodeConfig cfg, Object oldVal, Object newVal) {
        boolean rst = false;

        if (oldVal == null) {
            rst = true;
        } else {
            if (Math.abs((Float) newVal - (Float) oldVal) >= cfg.sensitive) {
                rst = true;
            }
        }
        return rst;
    }
}

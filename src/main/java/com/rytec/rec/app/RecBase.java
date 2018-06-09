package com.rytec.rec.app;

import com.rytec.rec.messenger.WebPush;
import com.rytec.rec.util.ConstantMessageType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基础类
 */
public abstract class RecBase {

    @Autowired
    private WebPush webPushObj;
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 直接发送对象
     *
     * @param msg
     */
    public void webPush(Object msg) {
        webPushObj.clientBroadcast(msg);
    }

    /**
     * 相WEB客户端发送消息
     *
     * @param msg
     */
    public void webNotify(int type, Object msg) {
        webPushObj.webNotify(type, msg);
    }

    /**
     * 向WEB端发送错误消息
     *
     * @param msg
     */
    public void webNotifyError(Object msg) {
        this.webNotify(ConstantMessageType.WEB_NOTIFY_MSG, msg);
    }

    /**
     * 调试消息
     *
     * @param msg
     */
    public void debug(String msg) {
        logger.debug(msg);
        webNotify(ConstantMessageType.WEB_NOTIFY_MSG, logger.getName() + '-' + msg);
    }

}

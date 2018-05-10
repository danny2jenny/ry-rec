package com.rytec.rec.app;

import com.rytec.rec.messenger.Message.WebMessage;
import com.rytec.rec.messenger.WebPush;
import com.rytec.rec.util.ConstantMessageType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基础类
 */
public abstract class RecBase {

    @Autowired
    public WebPush webPush;
    public org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 相WEB客户端发送消息
     *
     * @param msg
     */
    public void webNotify(String msg) {
        WebMessage webMessage = new WebMessage();
        webMessage.type = ConstantMessageType.WEB_NOTIFY_MSG;
        webMessage.msg = msg;

        webPush.clientBroadcast(webMessage);
    }

}

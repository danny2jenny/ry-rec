/**
 * Created by danny on 17-1-17.
 * <p>
 * 向ＷＥＢ客户端发送消息
 */

package com.rytec.rec.messenger;

import com.rytec.rec.messenger.Message.WebMessage;
import com.rytec.rec.util.ConstantMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebPush {

    @Autowired
    private SimpMessagingTemplate template;

    // 向web客户端广播消息
    public void clientBroadcast(Object msg) {
        template.convertAndSend("/topic/broadcast", msg);
    }


    /**
     * 相WEB客户端发送消息
     *
     * @param msg
     */
    public void webNotify(int type, Object msg) {
        WebMessage webMessage = new WebMessage();
        webMessage.type = type;
        webMessage.msg = msg;

        clientBroadcast(webMessage);
    }

    /**
     * 向WEB端发送错误消息
     * @param msg
     */
    public void webNotifyError(Object msg){
        this.webNotify(ConstantMessageType.WEB_NOTIFY_MSG, msg);
    }
}

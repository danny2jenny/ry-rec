/**
 * Created by danny on 17-1-17.
 * <p>
 * 向ＷＥＢ客户端发送消息
 */

package com.rytec.rec.messenger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebPush {

    @Autowired
    private SimpMessagingTemplate template;

    // 向web客户端广播消息
    public void clientBroadcast(String msg) {
        template.convertAndSend("/topic/broadcast", msg);
    }
}

package com.rytec.rec.service.Video;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.channel.ChannelMessage;
import com.rytec.rec.db.mapper.ChannelMapper;
import com.rytec.rec.db.model.Channel;
import com.rytec.rec.db.model.ChannelExample;
import com.rytec.rec.service.RyAbstractService;
import com.rytec.rec.service.RyTcpServer.RyTcpServer;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.util.AnnotationServiceType;
import com.rytec.rec.util.ConstantTcpCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Danny on 2017/3/13.
 */

@Service
@Order(300)
@AnnotationServiceType(101)
public class VideoService extends RyAbstractService implements ManageableInterface {

    @Autowired
    RyTcpServer ryTcpServer;

    @Autowired
    DbConfig dbConfig;

    @Autowired
    ChannelMapper channelMapper;

    /**
     * 初始化对应的HashMap
     * 两级 HashMap
     * 第一级：channelId->Map
     * 第二级：adr->ChannelNode
     */
    public HashMap<Integer, HashMap> channelAdrMapList = new HashMap();

    // nid -> channelNode
    public HashMap<Integer, ChannelNode> nodeMapList = new HashMap();


    @PostConstruct
    private void initConfig() {
        List<ChannelNode> chaNodeList = dbConfig.getChannelNodeList();


        for (ChannelNode cn : chaNodeList) {
            // 只管理 2000~3000 的channel
            if ((cn.getCtype() < 2000) || (cn.getCtype() > 3000)) {
                continue;
            }

            HashMap<Integer, ChannelNode> adrMapList = channelAdrMapList.get(cn.getId());
            if (adrMapList == null) {
                adrMapList = new HashMap();
            }

            adrMapList.put(cn.getAdr(), cn);

            nodeMapList.put(cn.getNid(), cn);
        }

    }

    public int sendMsg(ChannelMessage msg) {

        //通过 node 找到 channel 和 add，然后发送给RyTcpServer
        ChannelNode channelNode = nodeMapList.get(msg.nodeId);


        return 0;
    }

    @Override
    public void stop() {
        channelAdrMapList.clear();
        nodeMapList.clear();
    }


    @Override
    public void start() {
        initConfig();   // 初始化配置
        onLogin();      // 向Video服务发送配置
    }


    // 当登录的时候调用
    @Override
    public void onLogin() {
        ChannelExample channelExample = new ChannelExample();
        channelExample.createCriteria().andTypeBetween(2000, 3000);
        List<Channel> channels = channelMapper.selectByExample(channelExample);

        // 没有响应的配置，退出
        if (channels.size() == 0) {
            return;
        }

        String cfgStr = "";

        for (Channel item : channels) {
            cfgStr = cfgStr + item.getId() + ',' + item.getIp() + ',' + item.getPort() + ',' + item.getLogin() + ',' + item.getPass() + ',' + item.getType() + ';';
        }

        ByteBuf payload = Unpooled.buffer();
        payload.writeByte(ConstantTcpCommand.TCP_SEND_CFG);
        payload.writeCharSequence(cfgStr, Charset.forName("US-ASCII"));
        payload.writeByte(0x00);
        ryTcpServer.sendMsg(101, payload);
    }


}

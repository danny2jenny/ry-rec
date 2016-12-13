package com.rytec.rec.db

import com.alibaba.druid.pool.DruidDataSource
import com.rytec.rec.bean.ChannelNode
import com.rytec.rec.bean.DeviceNode
import groovy.sql.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 * Created by danny on 16-11-21.
 *
 * 通过数据库得到所有设备的配置
 */

@Service
class DbConfig {

    private final Logger logger = LoggerFactory.getLogger(DbConfig.class);

    private List<ChannelNode> channelNodeList

    private List<DeviceNode> deviceNodeList;

    @Autowired
    private DruidDataSource dataSource

    private initChannelNode() {
        //查询channel和node的对应集合
        def sqlStr = """
            select
                channel.id as id,
                channel.name as cname,
                ip,
                port,
                login,
                pass,
                channel.type as ctype,
                channel.other as channelConf,
                node.id as nid,
                node.add,
                no,
                node.name as nname,
                node.type as ntype,
                node.other as nodeConf,
                device,
                deviceFun
            from channel left join node on channel.id=node.cid
            """
        def sql = new Sql(dataSource);

        channelNodeList = []

        sql.eachRow(sqlStr) { item ->
            ChannelNode a = new ChannelNode()

            a.id = item.id
            a.cname = item.cname
            a.ip = item.ip
            a.port = item.port
            a.login = item.login
            a.pass = item.pass
            a.ctype = item.ctype
            a.channelConf = item.channelConf
            a.nid = item.nid
            a.add = item.add
            a.no = item.no
            a.nname = item.nname
            a.ntype = item.ntype
            a.nodeConf = item.nodeConf
            a.device = item.device
            a.deviceFun = item.deviceFun

            channelNodeList.add(a)
        }
    }

    private initDeviceNode() {
        def sqlStr = """
            select
                device.id as id,
                device.no as dno,
                device.name as dname,
                device.type as dtype,
                lnodetype,
                lnodenum,
                node.id as nid,
                cid,
                node.add as nadd,
                node.no as nno,
                node.type as ntype,
                other as conf,
                deviceFun as nfun
            from device
            left join node
            on device.id = node.device
            """
        def sql = new Sql(dataSource);
        deviceNodeList = sql.rows(sqlStr).toList();
    }

    List<ChannelNode> getChannelNodeList() {
        if (channelNodeList == null) {
            initChannelNode()
        }
        return channelNodeList
    }


    List<DeviceNode> getDeviceNodeList() {
        if (deviceNodeList == null) {
            initDeviceNode()
        }
        return deviceNodeList
    }

}

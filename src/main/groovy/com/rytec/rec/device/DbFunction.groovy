package com.rytec.rec.device

import com.alibaba.druid.pool.DruidDataSource
import com.rytec.rec.device.bean.ChannelNode
import groovy.sql.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 * Created by danny on 16-11-21.
 *
 * 通过数据库得到所有设备的配置
 */

@Service
class DbFunction {

    private final Logger logger = LoggerFactory.getLogger(DbFunction.class);

    @Autowired
    private DruidDataSource dataSource

    public getNodes() {
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
                no,
                node.name as nname,
                node.type as ntype,
                node.other as nodeConf,
                device,
                deviceFun
            from channel left join node on channel.id=node.cid
            """
        def sql = new Sql(dataSource);
        List<ChannelNode> a = sql.rows(sqlStr);
        return a.toString();
    }

    public getDevice() {
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
                node.no as nno,
                node.type as ntype,
                other as conf,
                deviceFun as nfun
            from device
            left join node
            on device.id = node.device
            """
    }
}

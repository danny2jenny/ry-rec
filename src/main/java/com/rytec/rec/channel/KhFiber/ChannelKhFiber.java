package com.rytec.rec.channel.KhFiber;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.Channel;
import com.rytec.rec.db.model.ChannelNode;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.node.NodeMessage;
import com.rytec.rec.util.AnnotationChannelType;
import com.rytec.rec.util.AnnotationJSExport;
import com.rytec.rec.util.ConstantFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 科华的光纤测温接口
 * <p>
 * 1、告警线路，整形
 * 2、告警位置，整形
 * 3、告警类型，遥信
 * 4、告警温度，浮点
 * <p>
 * https://github.com/alibaba/druid
 */
@Service
@Order(400)
@AnnotationChannelType(1003)
@AnnotationJSExport("科华光纤测温")
public class ChannelKhFiber extends RecBase implements ChannelInterface, ManageableInterface {

    @Autowired
    private DbConfig dbConfig;

    @Autowired
    public NodeManager nodeManager;

    /*
     * 两级 HashMap，保存Channel下面的Node
     * 第一级：channelId->Map
     * 第二级：nodeId->ChannelNode
     */
    public HashMap<Integer, HashMap> channelNodes = new HashMap();

    // 客户端对象
    // <ChannelId: DbSession>
    HashMap<Integer, DbSession> sqlServers = new HashMap();

    /**
     * 初始化
     */
    void getCfg() {

        // 初始化两级的Hash，保存每个Chanel下面的Node
        // ------------------ 初始化ChannelNode 的HashMap -----------------
        //
        List<ChannelNode> chaNodeList = dbConfig.getChannelNodeList();

        //第一级的Map channelId-> map
        for (ChannelNode cn : chaNodeList) {
            if (cn.getCtype() != 1003) {        //只管理该类型的Channel
                continue;
            }

            //是否已经存在该Channel
            HashMap<Integer, ChannelNode> cha = channelNodes.get(cn.getId());

            //不存在，建立该Channel
            if (cha == null) {
                cha = new HashMap();
                this.channelNodes.put(cn.getId(), cha);
            }

            //node的标识是 nid
            cha.put(cn.getNid(), cn);
        }

        // 初始化Channel的配置  建立 Sql 的DataSource
        List<Channel> channels = dbConfig.getChannelList();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String currentTime = df.format(new Date());
        //String currentTime = "2018-02-18 09:33:05";

        DruidDataSource khDataSource;
        DbSession dbSession;
        for (Channel channel : channels) {
            if (channel.getType() == 1003) {
                dbSession = new DbSession();
                khDataSource = new DruidDataSource();

                dbSession.id = channel.getId();
                dbSession.dataSource = khDataSource;
                dbSession.dataTime = currentTime;

                khDataSource.setUrl(channel.getIp());
                khDataSource.setUsername(channel.getLogin());
                khDataSource.setPassword(channel.getPass());
                sqlServers.put(channel.getId(), dbSession);
            }
        }
    }

    /**
     * 告警处理
     *
     * @param chaId
     * @param fiber
     * @param fiberSection
     * @param alarmPos
     * @param alarmType
     * @param alarmVal
     */
    void doAlarm(int chaId, int fiber, int fiberSection, int alarmPos, int alarmType, float alarmVal) {
        FiberVal fiberVal = new FiberVal();

        fiberVal.channel = fiber;
        fiberVal.section = fiberSection;
        fiberVal.position = alarmPos;
        fiberVal.type = alarmType;
        fiberVal.value = alarmVal;

        // 找到 Node的ID
        HashMap<Integer, ChannelNode> chans = channelNodes.get(chaId);
        if (chans == null) {
            return;
        }

        // 找到Node，并发送
        for (ChannelNode cn : chans.values()) {
            if ((cn.getAdr() == fiber) & (cn.getNo() == fiberSection)) {
                // 相 NodeManager 发送消息
                NodeMessage nodeMessage = new NodeMessage();
                nodeMessage.node = cn.getNid();
                nodeMessage.from = ConstantFromWhere.FROM_TIMER;
                nodeMessage.value = fiberVal;
                nodeManager.onMessage(nodeMessage);
                return;
            }
        }


    }

    /**
     * 定时检查数据库，并读取告警
     */
    @Scheduled(fixedDelay = 5000)
    void onTimeCheck() {
        for (DbSession dbSession : sqlServers.values()) {
            String sql = "select * from DataAlarm where SaveTime>'" + dbSession.dataTime + "'";
            try {
                DruidPooledConnection cn = dbSession.dataSource.getConnection();
                PreparedStatement stmt = cn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    doAlarm(
                            dbSession.id,                       // 设备通道号
                            rs.getInt(1),           // 光纤通道
                            rs.getInt(2),           // 分区号
                            rs.getInt(3),           // 位置
                            rs.getInt(4),           // 类型
                            rs.getInt(5)            // 温度
                    );
                    dbSession.dataTime = rs.getString(9);
                }

                rs.close();
                stmt.close();
                cn.close();
            } catch (SQLException e) {
                // 错误处理
                e.printStackTrace();
            }
        }

    }

    @Override
    @PreDestroy
    public void stop() {
        for (DbSession dbSession : sqlServers.values()) {
            dbSession.dataSource.close();
        }
        sqlServers.clear();
        channelNodes.clear();
    }

    @Override
    public void start() {
        getCfg();
    }

    @Override
    public int sendMsg(Object msg) {
        return 0;
    }

    @Override
    public void channelOnline(Object channelId, boolean online) {

    }
}

package com.rytec.rec.channel.KhFiber;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 保存每个数据库的状态
 */
public class DbSession {
    public int id;                          // 通道ID
    public DruidDataSource dataSource;
    public String lastAlarmTime;            // 最新的一个告警时间

    // 通道分区的对应关系
    private boolean initedd = false;         // 是否已经初始化
    // 通道id->分区记录
    public HashMap<Integer, FiberSection> sectionHashMap = new HashMap();

    public void init() {
        if (initedd)
            return;

        String sql = "SELECT  * FROM FiberSubarea";
        try {
            DruidPooledConnection cn = dataSource.getConnection();
            PreparedStatement stmt = cn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            FiberSection fc;
            while (rs.next()) {
                fc = new FiberSection();
                fc.id = rs.getInt(1);           // 分区ID，告警的时候也用这个标识
                fc.section = rs.getInt(2);      // 分区编号
                fc.firber = rs.getInt(3);       // 光纤通道
                fc.start = rs.getInt(5);        // 开始位置
                fc.end = rs.getInt(6);       // 结束位置
                sectionHashMap.put(fc.id, fc);
            }

            initedd = true;

        } catch (SQLException e) {
            initedd = false;
        }
    }
}

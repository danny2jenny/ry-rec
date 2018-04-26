package com.rytec.rec.channel.KhFiber;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 保存每个数据库的状态
 */
public class DbSession {
    public int id;                          // 通道ID
    public DruidDataSource dataSource;
    public String dataTime;
}

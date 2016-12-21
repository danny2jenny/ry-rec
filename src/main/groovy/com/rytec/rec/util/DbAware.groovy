package com.rytec.rec.util

import com.alibaba.druid.pool.DruidDataSource
import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * Created by danny on 16-12-20.
 * 操作数据库的基类
 */

@Service
class DbAware {
    @Autowired
    private DruidDataSource dataSource

    def Sql sql

    @PostConstruct
    void init() {
        sql = new Sql(dataSource)
    }
}

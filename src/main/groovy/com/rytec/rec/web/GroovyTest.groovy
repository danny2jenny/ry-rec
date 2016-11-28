package com.rytec.rec.web

import com.alibaba.druid.pool.DruidDataSource
import groovy.json.JsonSlurper
import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Created by danny on 16-11-7.
 */

@Controller
class GroovyTest {

    @Autowired
    private DruidDataSource dataSource

    @RequestMapping(value = "/groovyTest")
    @ResponseBody
    public String groovyController() {

        def jsonSlurper = new JsonSlurper();
        def object = jsonSlurper.parseText("""
            {
              "cmd": 105,
              "parm": {
                "cid": 2,
                "type": 6,
                "values": [
                  {
                    "node": 590081,
                    "val1": 20.730000
                  }
                ]
              }
            }
            """);

        def sql = new Sql(dataSource);
        def first = sql.firstRow("SELECT * FROM action");
        return first;
    }
}

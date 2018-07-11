package com.rytec.rec.service;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.Config;
import com.rytec.rec.util.ConstantCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 短消息服务
 */
@Service
@Order(400)
public class SMS implements ManageableInterface {

    @Autowired
    DbConfig dbConfig;

    // 电话号码列表
    List<String> mobileList = new ArrayList<>();

    @Override
    public void stop() {
    }

    @Override
    public void start() {
        mobileList.clear();
        List<Config> configs = dbConfig.getConfigList();
        for (Config config : configs) {
            if ((config.getCat() == ConstantCfg.OPT_SMS) && (config.getType() > 0)) {
                mobileList.add(config.getValue());
            }
        }
    }

    /**
     * 发送光纤测温告警
     *
     * @param smsBody
     */
    public void fiberSms(String smsBody) {
        for (String mobile : mobileList) {

            String cmd = String.format("sendsms 86%s %s", mobile, smsBody);
            // 写入到文件
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

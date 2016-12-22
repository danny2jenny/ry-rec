package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.rytec.rec.db.mapper.ConfigMapper;
import com.rytec.rec.db.model.Config;
import com.rytec.rec.db.model.ConfigExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by danny on 16-12-21.
 * 所有的数据库配置，需要转换成JS的配置
 */

@Controller
public class ExtOption {

    @Autowired
    ConfigMapper configMapper;

    @ExtDirectMethod
    public List<Config> getAll() {
        return configMapper.selectByExample(null);
    }

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<Config> list(ExtDirectStoreReadRequest request) {

        int cat = (Integer) request.getParams().getOrDefault("cat", 0);

        if (cat == 0) {
            return configMapper.selectByExample(null);
        } else {
            ConfigExample configExample = new ConfigExample();
            configExample.createCriteria().andCatEqualTo(cat);
            return configMapper.selectByExample(configExample);
        }

    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Config update(Config channel) {
        configMapper.updateByPrimaryKey(channel);
        return channel;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Config create(Config channel) {
        configMapper.insert(channel);
        return channel;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Config delete(Config channel) {
        configMapper.deleteByPrimaryKey(channel.getId());
        return channel;
    }

}

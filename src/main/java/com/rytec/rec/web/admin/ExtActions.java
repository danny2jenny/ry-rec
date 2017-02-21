package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.rytec.rec.db.mapper.ActionsMapper;
import com.rytec.rec.db.model.Actions;
import com.rytec.rec.db.model.ActionsExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


/**
 * Created by danny on 17-2-17.
 * <p>
 * Actions 的数据库维护
 */

@Controller
public class ExtActions {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ActionsMapper actionsMapper;

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<Actions> list(ExtDirectStoreReadRequest request) {
        ActionsExample actionsExample = new ActionsExample();
        actionsExample.createCriteria().andRuleEqualTo((Integer) request.getParams().get("masterId"));
        return actionsMapper.selectByExample(actionsExample);

    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Actions update(Actions actions) {
        actionsMapper.updateByPrimaryKey(actions);
        return actions;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Actions create(Actions actions) {
        actionsMapper.insert(actions);
        return actions;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Actions delete(Actions actions) {
        actionsMapper.deleteByPrimaryKey(actions.getId());
        return actions;
    }
}

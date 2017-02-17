package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import com.rytec.rec.db.mapper.ActionRuleMapper;
import com.rytec.rec.db.model.ActionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by danny on 17-2-17.
 * ActionRule 的数据库维护
 */

@Controller
public class ExtActionRule {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ActionRuleMapper actionRuleMapper;

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<ActionRule> list() {
        return actionRuleMapper.selectByExample(null);

    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public ActionRule update(ActionRule actionRule) {
        actionRuleMapper.updateByPrimaryKey(actionRule);
        return actionRule;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public ActionRule create(ActionRule actionRule) {
        actionRuleMapper.insert(actionRule);
        return actionRule;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public ActionRule delete(ActionRule actionRule) {
        actionRuleMapper.deleteByPrimaryKey(actionRule.getId());
        return actionRule;
    }
}

package com.rytec.rec.cooperate;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.RuleAction;
import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.util.ConstantErrorCode;
import com.rytec.rec.util.ConstantFromWhere;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by danny on 17-2-14.
 * <p>
 * 联动管理的服务
 * <p>
 * 1、Device收到Node的数据后进行相应的分析，然后把信号和参数发送到这里；
 * 2、收到信号和参数，和联动规则列表进行对比；
 * 3、创建一个联动的对象，
 * 4、联动对象在相应的Device上执行操作；
 */
@Service
@Order(300)
public class CooperateManager implements ManageableInterface {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DbConfig dbConfig;

    @Autowired
    DeviceManager deviceManager;

    /**
     * 从数据库中读取相应的联数据，然后保存为Map
     */

    /**
     * 两级Map
     * 第一级 "devicd:sig" -> Object(Map)
     * 第二级 targetid(devicdId) -> Action
     */
    private Map<String, List> mapRules = new HashMap();

    @PostConstruct
    void initConfig() {
        List<RuleAction> ruleActionList = dbConfig.getRuleActionList();

        for (RuleAction ruleAction : ruleActionList) {
            String raId = "" + ruleAction.getDevice() + ':' + ruleAction.getSig();

            List<RuleAction> actions = mapRules.get(raId);

            if (actions == null) {
                actions = new Vector();
                mapRules.put(raId, actions);
            }
            actions.add(ruleAction);

        }

    }

    /**
     * 告警信号处理
     *
     * @param deviceId 告警设备
     * @param sig      告警信号
     * @param parm     告警参数
     * @return 返回值
     */
    public int onSignal(int deviceId, int sig, Object parm) {

        String raId = "" + deviceId + ':' + sig;
        List<RuleAction> actions = mapRules.get(raId);
        if (actions == null) {
            return ConstantErrorCode.RULE_ACTION_NOT_EXIST;
        }

        // 按照联动的规则来进行相应的动作

        for (RuleAction action : actions) {
            if (action.getTarget()==null){
                continue;
            }
            AbstractOperator operator = deviceManager.getOperatorByDeviceId(action.getTarget());
            operator.operate(ConstantFromWhere.FROM_ALI, action.getTarget(), action.getAct(), action.getParm());
        }
        return 0;
    }

    public void stop() {
        mapRules.clear();
    }

    public void start() {
        initConfig();
    }

}

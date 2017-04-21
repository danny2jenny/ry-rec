package com.rytec.rec.web.user;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import com.rytec.rec.db.mapper.AlarmMapper;
import com.rytec.rec.db.model.Alarm;
import com.rytec.rec.db.model.AlarmExample;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


/**
 * Created by 12793 on 2017/4/14.
 */
@Controller
public class ExtAlarm {

    @Autowired
    AlarmMapper alarmMapper;

    // 分页查询所有的告警
    @ExtDirectMethod(ExtDirectMethodType.STORE_READ)
    public ExtDirectStoreResult<Alarm> list(ExtDirectStoreReadRequest request){
        AlarmExample alarmExample = new AlarmExample();
        alarmExample.setOrderByClause("id desc");
        RowBounds rowBounds = new RowBounds(request.getStart(),request.getLimit());
        int total = alarmMapper.countByExample(null);
        return new ExtDirectStoreResult<Alarm>(total,alarmMapper.selectByExampleWithRowbounds(alarmExample,rowBounds));
    }


}

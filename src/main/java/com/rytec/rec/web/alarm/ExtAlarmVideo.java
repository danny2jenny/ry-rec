package com.rytec.rec.web.alarm;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.rytec.rec.db.mapper.AlarmVideoMapper;
import com.rytec.rec.db.model.AlarmVideo;
import com.rytec.rec.db.model.AlarmVideoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by 12793 on 2017/4/20.
 * <p>
 * 通过device和sig查询相关联动的摄像机
 */
@Controller
public class ExtAlarmVideo {

    @Autowired
    AlarmVideoMapper alarmVideoMapper;

    /**
     * 查询联动的摄像机
     *
     * @param request
     * @return
     */
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<AlarmVideo> list(ExtDirectStoreReadRequest request) {
        AlarmVideoExample alarmVideoExample = new AlarmVideoExample();
        int device = (Integer) (request.getParams().get("device"));
        int sig = (Integer) (request.getParams().get("sig"));
        alarmVideoExample.createCriteria().andDeviceEqualTo(device).andSigEqualTo(sig);
        return alarmVideoMapper.selectByExample(alarmVideoExample);
    }
}

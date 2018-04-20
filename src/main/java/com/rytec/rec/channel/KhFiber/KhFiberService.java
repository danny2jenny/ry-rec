package com.rytec.rec.channel.KhFiber;

import com.rytec.rec.app.RecBase;
import com.rytec.rec.channel.KhFiber.db.mapper.KhDataAlarmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 科华的光纤测温接口
 *
 * 1、告警线路
 * 2、告警位置
 * 3、告警类型
 */
@Service
public class KhFiberService extends RecBase {

    @Autowired
    KhDataAlarmMapper khDataAlarmMapper;

    @Scheduled(fixedDelay = 2000)
    void test(){

        //logger.debug(khDataAlarmMapper.selectByExample(null).toString());
    }
}

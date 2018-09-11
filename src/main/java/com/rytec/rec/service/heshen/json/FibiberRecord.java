package com.rytec.rec.service.heshen.json;

import java.util.ArrayList;
import java.util.List;

/**
 * 测温光纤的数据
 */
public class FibiberRecord {
    String datetime;        // 更新时间
    float startpoint;       // 开始位置
    float distance;         // 间距
    int datacount;          // 数据个数
    List<Float> data = new ArrayList<Float>();
}

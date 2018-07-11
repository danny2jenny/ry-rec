package com.rytec.rec.channel.KhFiber;

/**
 * 通道分区记录
 * id 对应告警记录中的 SectNo
 */
public class FiberSection {
    public int id;             // 主键， id
    public int firber;         // 光纤通道
    public int section;        // 分区号
    public int start;          // 开始位置
    public int end;            // 结束位置
}

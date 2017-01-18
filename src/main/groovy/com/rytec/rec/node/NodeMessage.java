package com.rytec.rec.node;

import com.rytec.rec.util.CommandType;
import com.rytec.rec.util.FromWhere;
import com.rytec.rec.util.ValueType;

/**
 * Created by danny on 17-1-17.
 * <p>
 * Node的Msg格式
 */
public class NodeMessage {
    public int type = CommandType.GENERAL_READ;        //消息类型
    public int from = FromWhere.FROM_USER;             //消息来源
    public int node = 0;                               //节点
    public Object value = 0;                            //消息值

}

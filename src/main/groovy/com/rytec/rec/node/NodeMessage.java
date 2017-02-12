package com.rytec.rec.node;

import com.rytec.rec.util.ConstantCommandType;
import com.rytec.rec.util.ConstantFromWhere;

/**
 * Created by danny on 17-1-17.
 * <p>
 * Node的Msg格式
 */
public class NodeMessage {
    public int type = ConstantCommandType.GENERAL_READ;        //消息类型
    public int from = ConstantFromWhere.FROM_USER;             //消息来源
    public int node = 0;                                       //节点
    public Object value = 0;                                   //消息值

}

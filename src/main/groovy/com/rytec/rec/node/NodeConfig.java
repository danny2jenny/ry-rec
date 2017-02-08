package com.rytec.rec.node;

/**
 * Created by danny on 17-1-21.
 * <p>
 * Node 节点的配置类，这个类以json的方式保存在
 * 1、确定值的更新条件，当达到什么波动条件进行更新
 * 2、确定模拟量的转换参数，例如4~20mA，通过量程进行转换的必要参数
 */

public class NodeConfig {
    public float sensitive;            //更新条件：（oldValue-newValue）>= sensitive 的时候进行更新
    public float pA;                   //模拟量转换条件：Y=aX + B，输入值X为整形
    public float pB;                   //模拟量转换条件：Y=aX + B，输入值X为整形
}

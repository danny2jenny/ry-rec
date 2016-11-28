Rytec Remote Environment Surveillance
======================================

该系统采用全新的构架

# TODO

1、Websocket 通知框架

# 基本框架

servlet    3.1.0
核心框架    Spring 4.3.3
动态语言    Groovy 2.4.7
网络通讯    Netty 4.1.6
webUI      extjs ext 4.2.1.883
gisUI      openlayers 3.19.1
构建       gradle


# 基本思路

所有的外设全部采用现有的Modbus设备。目前得知的所有模拟量和开关量设备都有现成的。

http://www.usr.cn/
产品比较全，串口，网络转换的产品都有。

https://shop142026040.world.taobao.com/
温湿度便送器

https://34669493.world.taobao.com/
控制器等

https://34669493.world.taobao.com

在淘宝上输入modbus有很多现有的设备，

# modbus 模型

三种量：

开关输入、输出；
模拟输入、输出。

其中模拟的输出很少用到。






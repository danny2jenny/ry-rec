设备图标说明
====================

任何一个设备的图标有以下几个状态

* 失效状态 10
* 正常状态 11
* 关闭状态 20
* 开启状态 21
* 告警状态 31


所有设备图标使用gif格式（有些具备动画）

## 图标命名

NNN-SS.gif

* NNN：图标类型
* SS：状态编码

一套图标四个

## 动态图标制作

批量转换格式： mogrify    -format jpg   *.png

批量改变大小：mogrify -resize 16x12 -quality 100 -path ../new-thumbs *.jpg


convert -delay 50 11.png 11-1.png -loop 0 21.gif

## 格式转换







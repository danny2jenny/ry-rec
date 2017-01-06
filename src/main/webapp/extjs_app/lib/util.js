/**
 * Created by danny on 16-12-21.
 *
 * 工具
 */

ry = new Object();

//通过index翻译成对应的值
ry.trans = function (index, type) {
    for (var i = 0; i < type.length; i++) {
        if (type[i][0] == index) {
            return type[i][1];
        }
    }
    return "*未知*";
}

//channel cat=1
ry.CHANNEL_TYPE = [];
//node cat = 2
ry.NODE_TYPE = [];
//deivce cat =3
ry.DEVICE_TYPE = [];
//deivceFun cat =4
ry.DEVICE_FUN = [];
//deivceFun cat =5
ry.DEVICE_ICON = [];

ry.OPT_CAT = [
    [1, '通道类型'],
    [2, '节点类型'],
    [3, '设备基础类型'],
    [4, '端口功能'],
    [5, '设备图标集']

]

ry.GIS_FEATURE_TYPE = [
    [1, '点'],
    [2, '线'],
    [3, '面']
];

// 得到数据库中的配置
ry.onGetOption = function (data, caller, result) {
    //初始化配置
    for (var i = 0; i < data.length; i++) {

        switch (data[i].cat) {
            // 通道类型
            case 1:
                ry.CHANNEL_TYPE.push([data[i].value, data[i].name]);
                break;
            // 节点类型
            case 2:
                ry.NODE_TYPE.push([data[i].value, data[i].name]);
                break;
            // 设备类型
            case 3:
                ry.DEVICE_TYPE.push([data[i].value, data[i].name]);
                break;
            // 端口功能
            case 4:
                ry.DEVICE_FUN.push([data[i].value, data[i].name]);
                break;
            case 5:
                ry.DEVICE_ICON.push([data[i].value, data[i].name]);
                break;
        }
    }
}
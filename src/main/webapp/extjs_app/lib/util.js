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
    return "未知类型";
}

//channel cat=1
ry.CHANNEL_TYPE = [];
//node cat = 2
ry.NODE_TYPE = [];
//deivce cat =3
ry.DEVICE_TYPE = [];

ry.OPT_CAT = [
    [1, '通道类型'],
    [2, '节点类型'],
    [3, '设备类型'],
]

// 得到数据库中的配置
ry.onGetOption = function (data, caller, result) {
    //初始化配置
    for (var i = 0; i < data.length; i++) {

        switch (data[i].cat){
            case 1:
                ry.CHANNEL_TYPE.push([data[i].value, data[i].name]);
                break;
            case 2:
                ry.NODE_TYPE.push([data[i].value, data[i].name]);
                break;
            case 3:
                ry.DEVICE_TYPE.push([data[i].value, data[i].name]);
                break;
        }
    }
}


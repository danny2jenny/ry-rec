/**
 * Created by danny on 16-12-21.
 *
 * 工具
 */

ry = new Object();

/*
 * Device 的管理额对象列表
 * 'devicd_XXX'->Object
 * 其中 XXX 是Device的类型编号
 */

ry.devices = {};
// 存放Device编辑器下拉选项
ry.deviceEditor = {};

//通过index翻译成对应的值
ry.trans = function (index, type) {
    for (var i = 0; i < type.length; i++) {
        if (type[i][0] == index) {
            return type[i][1];
        }
    }
    return "*未知*";
};


ry.CHANNEL_TYPE = [];       //channel cat=1
ry.NODE_TYPE = [];          //node cat = 2
ry.DEVICE_TYPE = [];        //deivce cat =3
ry.DEVICE_FUN = [];         //deivceFun cat =4
ry.DEVICE_ICON = [];        //deivceFun cat =5

ry.OPT_CAT = [
    [11, '设备图标集']
]

ry.GIS_FEATURE_TYPE = [
    [1, '点'],
    [2, '线'],
    [3, '面']
];

/**
 * Stomp
 *
 * 最早的项目 https://github.com/jmesnil/stomp-websocket
 * 还在更新的 https://github.com/crossz/stomp-websocket
 * 评价比较好的fork https://github.com/JSteunou/webstomp-client
 *
 */
ry.stom = new Object();
ry.stom.stomp_url = 'ws://' + window.location.host + '/srv/stomp/websocket';
//ry.stom.client = Stomp.client(ry.stom.stomp_url);

// 连接建立后
ry.stom.connect_callback = function () {
    console.log('STOM 连接建立。。。。');
    // 订阅消息
    ry.stom.msgChannel = ry.stom.client.subscribe("/topic/broadcast", ry.stom.onMsg);
};

// 连接建立失败
ry.stom.error_callback = function (error) {
    console.log('STOM 连接错误！！！！！');
};

// 收到消息
ry.stom.onMsg = function (msg) {
    console.log('STOM 收到消息:' + msg);
    var msgObject = JSON.parse(msg.body);
    switch (msgObject.type) {
        case 401:           // Device 消息
            var msg = msgObject.msg;
            var deviceId = msg.device;
            // 更新GIS中Device的状态
            ry.gis.updateDeviceState(msg);
            break;
        case 501:           // Node 消息

            break;
        case 601:           // Channel 消息

            break;
    }
};

/**
 * client.send 的第二个参数是优先级，例如 {priority: 9}
 * @param msg
 */
ry.stom.sendMsg = function (msg) {
    ry.stom.client.send("/topic/broadcast", {}, msg);
};

// 定时检查连接是否建立
ry.stom.keepConnectRunner = function () {

    if (ry.stom.client && !ry.stom.client.connected) {
        ry.stom.connect();
    }

    if (!ry.stom.client) {
        ry.stom.connect();
    }
};

// 建立Stom连接
ry.stom.connect = function () {
    // 不知道为什么，以下两句必须一同执行，否者不能连接
    ry.stom.client = Stomp.client(ry.stom.stomp_url);
    ry.stom.client.connect(null, null, ry.stom.connect_callback, ry.stom.error_callback);
}

// 启动定时任务
ry.timeTask = new Ext.util.TaskRunner();
ry.timeTask.start({
    run: ry.stom.keepConnectRunner,
    interval: 1000
});




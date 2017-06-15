/**
 * Created by danny on 16-12-21.
 *
 * 工具
 */


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

ry.OPT_CAT = [
    [11, '设备图标集']
]

ry.GIS_FEATURE_TYPE = [
    [1, '点'],
    [2, '线'],
    [3, '面']
];
// ******************************** 常用函数 ****************************

/**
 *
 * @param icon               图标的代码
 * @param iconState               图标模式
 * @returns {string}        返回图标的路径
 */
ry.getDeviceStateIcon = function (icon, iconState) {
    return "res/gis/device/" + icon + "-" + iconState + ".gif";
};

// ******************************** 视频播放 ****************************

/**
 * 在Grid中实时播放
 * @param device
 */
ry.realPlayInGrid = function (device) {
    var cfg = Ext.StoreMgr.get('NvrNode');
    var rec = cfg.findRecord('id', device);
    if (!rec) {
        return;
    }
    if (typeof(videoPlayer) != 'undefined') {
        videoPlayer.realPlayInGrid(rec.data.cid, rec.data.nadd)
    }
};

/**
 * 在新窗体中实时播放
 * @param device
 */
ry.realPlayInForm = function (device) {
    var cfg = Ext.StoreMgr.get('NvrNode');
    var rec = cfg.findRecord('id', device);
    if (!rec) {
        return;
    }
    if (typeof(videoPlayer) != 'undefined') {
        videoPlayer.realPlayInForm(rec.data.cid, rec.data.nadd)
    }
};

/**
 * 历史回放窗口
 * @param device
 */
ry.playBack = function (device) {
    var cfg = Ext.StoreMgr.get('NvrNode');
    var rec = cfg.findRecord('id', device);
    if (!rec) {
        return;
    }
    if (typeof(videoPlayer) != 'undefined') {
        videoPlayer.playBack(rec.data.cid, rec.data.nadd, rec.data.dname)
    }
};


ry.devicesState = null;
/**
 * 获取DeviceState
 * @param scope  // 调用的对象，需要有onDeviceStates方法
 */
ry.getDeviceStates = function (scope) {
    gisDevice.getDevicesState(function (data, event, rst) {
        ry.devicesState = data;
        // 如果有回调函数，执行回调函数
        if (this.onDevicesState) {
            this.onDevicesState(data);
        } else {
            // 更新GIS
            var pa = Ext.getCmp("panel.panorama.editor");
            if (pa) pa.updateAllIcon();
            if (ry.gis) ry.gis.overlay.updateDevice();
        }
    }, scope);
};

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
    // 需要刷新DeviceState
    ry.getDeviceStates();
};

// 连接建立失败
ry.stom.error_callback = function (error) {
    console.log('STOM 连接错误！！！！！');
};

// 收到消息
ry.stom.onMsg = function (msg) {
    var msgObject = JSON.parse(msg.body);
    switch (msgObject.type) {
        case ry.CONST.MSG_TYPE.DEVICE_STATE:           // Device 消息
            var msg = msgObject.msg;
            var deviceId = msg.device;
            // 更新GIS中Device的状态
            ry.gis.updateDeviceState(msg);
            if (ry.panorama) {
                ry.panorama.updateDeviceState(msg);
            }
            break;
        case ry.CONST.MSG_TYPE.NODE_STATE:           // Node 消息

            break;
        case ry.CONST.MSG_TYPE.CHANNEL_STATE:           // Channel 消息

            break;
        case ry.CONST.MSG_TYPE.DEVICE_ALARM:            // 告警消息
            var ap = Ext.getCmp('user.alarm.panel');
            ap.alarm(msgObject);
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
    ry.stom.client.debug = function(str) {

    };
    ry.stom.client.connect(null, null, ry.stom.connect_callback, ry.stom.error_callback);
}

// 启动定时任务
ry.timeTask = new Ext.util.TaskRunner();
ry.timeTask.start({
    run: ry.stom.keepConnectRunner,
    interval: 1000
});

// 服务器重新读取配置
ry.serverReload = function () {
    var url = window.document.location.origin + "/srv/system";
    oAjax = new XMLHttpRequest();
    oAjax.open('GET', url, true);
    oAjax.send();
    console.log("System reload....");
};

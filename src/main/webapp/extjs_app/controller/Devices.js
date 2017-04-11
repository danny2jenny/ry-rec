/**
 * Created by danny on 17-4-10.
 */


// 加载Device所需要的JS文件
for (var i in ry.DEVICE_TYPE) {
    var type_id = ry.DEVICE_TYPE[i][0];
    Ext.Loader.loadScript('extjs_app/device/Device_' + type_id + '.js');
}

Ext.define('app.controller.Devices', {
    extend: 'Ext.app.Controller',

    init: function () {

        // 初始化视频播放客户端
        Ext.direct.Manager.on('event', function (event, provider, eOpts) {

            // 得到NVR的配置
            if (event.action == "extVideo" && event.method == "listNvr") {
                var cfg = JSON.stringify(event.result);

                if (typeof(videoPlayer) == "undefined") {
                    return;
                }

                videoPlayer.initConfig(cfg);
            }
        });
    }
});
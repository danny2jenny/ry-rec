/**
 * Created by danny on 17-4-11.
 *
 * 摄像机
 */


ry.devices['device_401'] = {
    gisClick: function (fProperties) {
        var did = fProperties.deviceId;
        // 找到相应的adr和cid
        var cfg = Ext.StoreMgr.get('NvrNode').data.items

        for (var i in cfg) {
            if (cfg[i].data.id == did) {
                ry.playRealVideo(cfg[i].data.cid, cfg[i].data.nadd);
                break;
            }
        }


    }
};
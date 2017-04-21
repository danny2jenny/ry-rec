/**
 * Created by 12793 on 2017/4/20.
 *
 * 告警相关的视频列表
 */

Ext.define('app.model.AlarmVideo', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'device'},
        {name: 'sig'},
        {name: 'target'},
        {
            name: 'icon',
            convert: function (value, record) {
                if (!ry.gis.overlay.devicesState) {
                    return ry.getDeviceStateIcon(0, ry.CONST.DEVICE_STATE.STATE_NORMA);
                }
                if (record.raw) {
                    return ry.getDeviceStateIcon(ry.gis.overlay.devicesState[record.raw.target].device.icon, ry.CONST.DEVICE_STATE.STATE_NORMAL);
                } else {
                    return "NULL"
                }

            }
        }
    ]
});

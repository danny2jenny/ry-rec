/**
 * Created by 12793 on 2017/4/14.
 */

Ext.define('app.model.Alarm', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'device'},
        {name: 'sig'},
        {name: 'value'},
        {name: 'time'}, {
            name: 'hour',
            convert: function (value, record) {
                if (record.raw) {
                    return Ext.Date.format(new Date(record.raw.time), 'H:i');
                } else {
                    return "NULL"
                }

            }
        }, {
            name: 'day',
            convert: function (value, record) {
                if (record.raw) {
                    return Ext.Date.format(new Date(record.raw.time), 'Y-m-d');
                } else {
                    return "NULL"
                }

            }
        }, {
            name: 'icon',
            convert: function (value, record) {
                if (!ry.devicesState) {
                    return ry.getDeviceStateIcon(0, ry.CONST.DEVICE_STATE.STATE_NORMA);
                }
                if (record.raw) {
                    return ry.getDeviceStateIcon(ry.devicesState[record.raw.device].device.icon, ry.CONST.DEVICE_STATE.STATE_NORMAL);
                } else {
                    return "NULL"
                }

            }
        }
    ]
});

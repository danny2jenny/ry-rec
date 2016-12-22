/**
 * 这里加载所有的数据的View、models、stores
 *
 * 注意，Models应该不用加载，因为在Store中会加载Model
 */

Ext.define('ryrec.controller.AdminFrame', {
    extend: 'Ext.app.Controller',
    views: [
        'admin.panel.ChannelNode',
        'admin.DeviceGrid',
        'admin.panel.Option'
    ],

    stores: [
        'admin.Channel',
        'admin.Node',
        'Device',
        'Option',
        'option.ChannelType'
    ],

    init: function () {

    }
});

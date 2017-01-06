/**
 * 这里加载所有的数据的View、models、stores
 *
 * 注意，Models应该不用加载，因为在Store中会加载Model
 */

Ext.define('app.controller.AdminFrame', {
    extend: 'Ext.app.Controller',
    views: [
        'admin.frame.ChannelNode',
        'admin.frame.DeviceNodeGrid',
        'admin.panel.Option',
        'gis.GisView',
        'admin.panel.Channel',
        'admin.panel.Node',
        'admin.panel.Device',
        'admin.panel.NodeForDevice',
        'admin.panel.Gis',
        'admin.frame.GisEditor',
        'admin.panel.FormLayerUpload'
    ],

    stores: [
        'admin.Channel',
        'admin.Node',
        'NodeForDevice',
        'Device',
        'Option',
        'option.ChannelType',
        'Gis'
    ],

    init: function () {

    }
});

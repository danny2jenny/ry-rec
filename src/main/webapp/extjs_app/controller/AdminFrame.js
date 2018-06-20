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
        'gis.GisEditor',
        'admin.panel.Channel',
        'admin.panel.Node',
        'admin.panel.Device',
        'admin.panel.NodeForDevice',
        'admin.panel.Gis',
        'admin.frame.GisEditor',
        'admin.panel.FormLayerUpload',
        'admin.panel.GisLayer',
        'admin.panel.NodeConfig',
        'admin.panel.ActionRule',
        'admin.panel.Actions',
        'admin.frame.Cooperate',
        // 全景
        'admin.panel.Panorama',
        'admin.panel.PanoramaGrid',
        'admin.panel.PanoramaUpload',
        'admin.frame.PanoramaEditor',
        // 端口重定向和61850
        'admin.panel.AllNode',
        'admin.panel.NodeRedirect',
        'admin.frame.NodeRedirect',
        'admin.panel.IcdUpload'

    ],

    stores: [
        'Channel',
        'Node',
        'NodeAll',
        'NodeForDevice',
        'Device',
        'Option',
        'ChannelType',
        'Gis',
        'GisLayer',
        'Actions',
        'ActionRule',
        'Nvr',
        'NvrNode',
        'Panorama',
        'PanoramaDevice',
        'NodeRedirect'
    ],

    init: function () {

    }
});

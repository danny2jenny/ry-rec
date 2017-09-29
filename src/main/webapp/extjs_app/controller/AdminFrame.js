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
        'admin.panel.Iec61850'

    ],

    stores: [
        'Channel',
        'Node',
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
        'PanoramaDevice'
    ],

    init: function () {

    }
});

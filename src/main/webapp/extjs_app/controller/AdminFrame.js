/**
 * 这里加载所有的数据的View、models、stores
 *
 * 注意，Models应该不用加载，因为在Store中会加载Model
 */

Ext.define('ryrec.controller.AdminFrame', {
    extend: 'Ext.app.Controller',
    views: [
        'admin.panel.ChannelNode',
    ],

    stores: [
        'admin.Channel',
        'admin.Node'
    ],

    init: function () {

    }
});

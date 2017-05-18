/**
 * Created by 12793 on 2017/4/14.
 */
Ext.define('app.controller.UserFrame', {
    extend: 'Ext.app.Controller',
    views: [
        'gis.GisView',
        'user.AlarmPanel',
        'window.Panorama',
        // 全景
        'admin.panel.Panorama'
    ],

    stores: [
        'GisLayer',
        'Nvr',
        'NvrNode',
        'Alarm',
        'AlarmVideo',
        'Panorama',
        'PanoramaDevice'
    ],

    init: function () {

    }
});
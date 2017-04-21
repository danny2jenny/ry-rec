/**
 * Created by danny on 16-12-27.
 */

Ext.define('app.view.gis.GisEditor', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.gis.editor',
    itemId: 'gis_editor',
    id: 'gis.editor',

    requires: [
        'app.lib.GisViewPlugin'
    ],

    plugins: [{
        ptype: 'gis.view',
        layerStore: 'GisLayer',
        editable: true
    }],

    initComponent: function () {
        this.callParent(arguments);
        Ext.direct.Manager.on('event', function (event, provider, eOpts) {
            // 当 Gis Feature 添加，应该刷新 GisGrid
            if (event.action == 'extGis' && event.method == 'saveFeature') {
                Ext.getCmp('admin.panel.gis').editPlugin.reload();
            }

            // 当删除一个Gis，应该删除相应的Feature和Overlay
            if (event.action == 'extGis' && event.method == 'delete') {
                Ext.getCmp('gis.view').gis.deleteFeature(event.result.id);
            }

            // 当删除一个图层s，应该刷新图层？
            if (event.action == 'extGisLayer' && event.method == 'delete') {

            }
        })
    }
});
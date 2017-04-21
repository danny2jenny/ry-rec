/**
 * Created by danny on 17-1-5.
 */

Ext.define('app.view.admin.frame.GisEditor', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.giseditor',
    layout: 'border',
    icon: 'res/toolbar/gis.png',
    title: 'GIS 编辑',
    items: [
        {
            xtype: 'gis.editor',
            region: 'center'
        },
        {
            xtype: 'panel',
            region: 'east',
            width: 300,
            layout: 'border',
            items: [
                {
                    xtype: 'admin.panel.gislayer',
                    region: 'center',
                },
                {
                    xtype: 'admin.panel.formlayerupload',
                    title: '上传图层',
                    region: 'south',
                    height: 200
                }
            ]
        }],
    initComponent: function () {

        var me = this;

        me.callParent(arguments);

        // 隐藏Device的所有配置Panel
        this.on('show', function (from, eOpts) {
            Ext.ComponentQuery.query('#griddeviceNode')[0].hideAllSouthPanel();
            var gis = Ext.ComponentQuery.query('#admin_panel_gis')[0];
            gis.show();
            this.down('#gis_editor').refresh();
        })
    }
});

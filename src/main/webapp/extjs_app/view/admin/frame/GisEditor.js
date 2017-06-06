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

    // 所属的Grid的选择事件
    onSelectChange: function (view, selections, options) {
        if (selections.length) {
            this.layerId = selections[0].data.id;
            this.down('#btn_replace').enable();
        } else {
            this.down('#btn_replace').disable();
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        var layer_list = this.down('#grid_gislayer');
        var form_upload = this.down('#formLayerUpload');

        layer_list.on('selectionchange', this.onSelectChange, form_upload);

        // 隐藏Device的所有配置Panel
        this.on('show', function (from, eOpts) {
            Ext.ComponentQuery.query('#griddeviceNode')[0].hideAllSouthPanel();
            var gis = Ext.ComponentQuery.query('#admin_panel_gis')[0];
            gis.show();
            this.down('#gis_editor').refresh();

            ry.serverReload();
        })
    }
});

/**
 * Created by danny on 17-5-10.
 */

Ext.define('app.view.admin.frame.PanoramaEditor', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.panoramaeditor',
    layout: 'border',
    icon: 'res/toolbar/panorama.png',
    title: '全景编辑',
    items: [
        {
            xtype: 'admin.panel.Panorama',
            region: 'center'
        },
        {
            xtype: 'panel',
            region: 'east',
            width: 300,
            layout: 'border',
            items: [
                {
                    xtype: 'admin.panel.panoramagrid',
                    region: 'center',
                },
                {
                    xtype: 'admin.panel.panoramaupload',
                    title: '场景上传',
                    region: 'south',
                    height: 200
                }
            ]
        }],

    // Panorama Grid的选择事件
    onPanoramaSelectChange: function (view, selections, options) {
        if (selections.length) {
            this.sceneId = selections[0].data.id;
            this.down('#btn_replace').enable();

            // 加载场景
            var panorama = Ext.getCmp('panel.panorama.editor');
            ry.panorama.loadPanorama(selections[0].get('device'));
        } else {
            this.down('#btn_replace').disable();
        }
    },

    // device 选择事件
    onDeviceSelectChange: function (view, selections, options) {
        if (selections[0].data.type == 9999) {
            this.down('#btn_new').enable();
            this.deviceId = selections[0].data.id;
        } else {
            this.down('#btn_new').disable();
            this.deviceId = 0;
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        var panorama_list = this.down('#grid_panoramagrid');
        var form_upload = this.down('#admin_panel_panoramaupload');
        panorama_list.on('selectionchange', this.onPanoramaSelectChange, form_upload);

        // device grid 选择事件
        var device_grid = Ext.getCmp('admin.device.grid');

        device_grid.on('selectionchange', this.onDeviceSelectChange, form_upload);
    }
});

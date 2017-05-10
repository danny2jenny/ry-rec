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

    // 所属的Grid的选择事件
    onSelectChange: function (view, selections, options) {
        if (selections.length) {
            this.sceneId = selections[0].data.id;
            this.down('#btn_replace').enable();
        } else {
            this.down('#btn_replace').disable();
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        var panorama_list = this.down('#grid_panoramagrid');
        var form_upload = this.down('#admin_panel_panoramaupload');

        panorama_list.on('selectionchange', this.onSelectChange, form_upload);
    }
});

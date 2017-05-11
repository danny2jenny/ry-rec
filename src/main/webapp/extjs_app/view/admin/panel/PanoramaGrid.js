/**
 * Created by danny on 17-5-9.
 * 全景场景的Grid列表
 */


Ext.define('app.view.admin.panel.PanoramaGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.panoramagrid',
    id: 'admin.panel.panoramagrid',
    itemId: 'grid_panoramagrid',
    title: '全景场景',
    icon: 'res/toolbar/panorama.png',
    store: 'Panorama',

    columns: [
        {
            text: 'ID',
            width: 60,
            dataIndex: 'id'
        },
        {
            text: '场景名称',
            dataIndex: 'name',
            flex: 1,
            editor: {
                allowBlank: false
            }
        }, {
            text: '设备',
            dataIndex: 'device',
            width: 60,
            editor: {
                allowBlank: false
            }
        }
    ],

    plugins: [{
        ptype: 'grid.editing',
        hideAdd: true,
        autoLoad: true
    }]
});
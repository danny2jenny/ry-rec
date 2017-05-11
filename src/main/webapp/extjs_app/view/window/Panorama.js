/**
 * Created by danny on 17-5-2.
 *
 * 全景展示窗口
 * https://github.com/mpetroff/pannellum
 *
 * 每个Device包含以下关键变量
 * hotspot-id
 * device-id
 * name->text
 * icon
 * type
 *
 * 通过DeviceState来更新状态图标
 *
 */

Ext.define('app.view.window.Panorama', {
    extend: 'Ext.window.Window',
    alias: 'widget.window.panorama',
    id: 'window.panorama',
    width: 640,
    height: 480,
    layout: 'fit',
    maximized: true,
    icon: 'res/toolbar/panorama.png',

    items: [
        {
            xtype: 'admin.panel.Panorama',
            editable: false
        },
    ],

    initComponent: function () {
        this.callParent(arguments);
    }
})
;
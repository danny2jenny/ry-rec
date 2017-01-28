/**
 * Created by danny on 17-1-4.
 *
 * Device 列表
 */

Ext.define('app.view.admin.panel.Device', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.device',

    title: '设备列表',
    icon: '/icon/toolbar/device.png',
    store: 'Device',
    itemId: 'adminDeviceGrid',

    plugins: [{
        ptype: 'grid.editing',
        autoLoad: true
    }],

    // 分组
    features: [Ext.create('Ext.grid.feature.Grouping', {
        groupHeaderTpl: ['{groupValue:this.formatValue}: 共 ({rows.length}) 个', {
            formatValue: function (value) {
                return '<img src="/icon/device/' + value + '.png">' + ry.trans(value, ry.DEVICE_TYPE)
            }
        }]
    })],

    columns: [
        {
            text: '名称',
            width: 40,
            sortable: false,
            dataIndex: 'name',
            menuDisabled: true,
            flex: 1,

            editor: {
                allowBlank: false
            }
        },
        {
            text: '类型',
            dataIndex: 'type',
            editor: {
                xtype: 'combobox',
                store: ry.DEVICE_TYPE,
            },
            renderer: function (val, column, row) {
                return ry.trans(val, ry.DEVICE_TYPE);
            }
        },
        {
            text: '图标',
            dataIndex: 'icon',
            editor: {
                allowBlank: false,
                xtype: 'combobox',
                store: ry.DEVICE_ICON
            },
            renderer: function (val, column, row) {

                return "<img src='/icon/device_icon/" + val + "-11.gif'>"
            }
        }
    ],

    viewConfig: {
        plugins: {
            ddGroup: 'ddg_node',
            ptype: 'gridDragPlugin',
            enableDrop: true,
            enableDrag: false
        }
    }
})

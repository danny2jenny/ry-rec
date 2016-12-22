/**
 * Created by danny on 16-12-21.
 *
 * 设备编辑列表
 * 设备按照类型进行分组
 * Store中必须设置分组条件：groupField: 'type',
 */

Ext.define('ryrec.view.admin.DeviceGrid', {
    extend: 'ryrec.lib.MasterSlaveGride',
    alias: 'widget.deviceGrid',
    title: '设备列表',
    icon: '/icon/toolbar/device.png',
    //hideHeaders: true,

    store: 'Device',

    // 定义 colums
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
            text: '编号',
            dataIndex: 'no',
            editor: {
                allowBlank: false
            }
        }
    ],


    // 分组
    features: [Ext.create('Ext.grid.feature.Grouping', {
        groupHeaderTpl: ['{groupValue:this.formatValue}: 共 ({rows.length}) 个', {
            formatValue: function (value) {
                return "分类"
            }
        }]
    })]


})
/**
 * Created by danny on 17-1-4.
 *
 * Device 列表
 */

Ext.define('app.view.admin.panel.Device', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.device',
    title: '设备列表',
    icon: 'res/toolbar/device.png',
    store: 'Device',
    itemId: 'adminDeviceGrid',
    id: 'admin.device.grid',

    plugins: [{
        ptype: 'grid.editing',
        autoLoad: true
    }],

    // 分组
    features: [Ext.create('Ext.grid.feature.Grouping', {
        groupHeaderTpl: ['{groupValue:this.formatValue}: 共 ({rows.length}) 个', {
            formatValue: function (value) {
                return '<img height="16" width="16" src="res/device/' + value + '.png">' + ry.trans(value, ry.DEVICE_TYPE)
            }
        }]
    })],

    columns: [
        {
            text: 'ID',
            dataIndex: 'id',
            width: 30
        },
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
            width: 100,
            editor: {
                allowBlank: false,
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
            width: 40,
            editor: {
                allowBlank: false,
                xtype: 'combobox',
                store: ry.DEVICE_ICON
            },
            renderer: function (val, column, row) {
                return "<img height=16 width=16 src='res/gis/device/" + val + "-11.gif'>"
            }
        }
    ],

    tools: [{
        type: 'refresh',
        tooltip: '当修改了设备、通道、节点、联动后需要保存配置才能生效！',
        // hidden:true,
        handler: function (event, toolEl, panelHeader) {
            ry.serverReload();
        }
    }],

    viewConfig: {
        plugins: {
            dropGroup: 'groupNode',
            dragGroup: 'groupDevice',
            ptype: 'gridDragPlugin',
            showField: 'name',
            enableDrop: true,
            enableDrag: true
        }
    },

    onSelectChange: function (view, selections, options) {
        if (!selections.length) {
            return
        }
        var device = selections[0].getId();
        ry.gis.highlightDevice(device);
    },

    initComponent: function () {
        this.callParent(arguments);
        this.on('selectionchange', this.onSelectChange, this);
    }
});

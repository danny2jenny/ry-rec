Ext.define('app.view.admin.panel.Iec61850', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.iec61850',
    title: '61850上报',
    icon: 'res/toolbar/device.png',
    store: 'Device',
    //itemId: 'adminDeviceGrid',

    plugins: [{
        ptype: 'grid.editing',
        autoLoad: true,
        hideAdd: true,
        hideDel: true
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
            width: 80,
            editor: {
                allowBlank: false,
                xtype: 'combobox',
                store: ry.DEVICE_ICON
            },
            renderer: function (val, column, row) {
                return "<img height=16 width=16 src='res/gis/device/" + val + "-11.gif'>"
            }
        }, {
            xtype: 'checkcolumn',
            text: '61850',
            width: 80,
            dataIndex: 'iec61850',
            stopSelection: false
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

    initComponent: function () {
        this.callParent(arguments);

    }
});
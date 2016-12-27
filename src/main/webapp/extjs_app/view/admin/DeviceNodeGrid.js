/**
 * Created by danny on 16-12-26.
 *
 * Device 和 Node 的编辑
 */

Ext.define('app.view.admin.DeviceNodeGrid', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.devicechannelnode',
    layout: 'border',
    // icon: 'icon/toolbar/control.png',
    // title: '设备编辑',
    itemId: 'griddeviceNode',
    items: [
        {
            xtype: 'gridEditBase',
            region: 'center',
            title: '设备列表',
            icon: '/icon/toolbar/device.png',
            store: 'Device',
            itemId: 'adminDeviceGrid',
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
                    text: '编号',
                    dataIndex: 'no',
                    editor: {
                        allowBlank: false
                    }
                }
            ]
        },

        // 子表设置
        {
            xtype: 'gridEditBase',
            region: 'south',
            height: 300,
            //margins: '0 0 5 0',
            split: true,
            title: '已经配置的节点',
            icon: 'icon/toolbar/node.png',
            // 定义 Model
            store: 'NodeForDevice',
            // 定义 autoload
            autoload: false,
            // 主表id
            master: 'adminDeviceGrid',
            // 定义 外键colums
            fkey: 'device',
            columns: [
                {
                    text: 'ID',
                    dataIndex: 'id'
                },

                {
                    text: '名称',
                    dataIndex: 'name',
                    width: 100,
                    editor: {
                        allowBlank: false
                    }
                },

                {
                    text: '端口功能',
                    dataIndex: 'devicefun',
                    editor: {
                        xtype: 'combobox',
                        store: ry.DEVICE_FUN
                    },
                    renderer: function (val, column, row) {
                        return ry.trans(val, ry.DEVICE_FUN);
                    }
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        // Device 的选择影响到 Node 的按钮
        this.down('#adminDeviceGrid').on('selectionchange', function (view, records) {
            Ext.ComponentQuery.query('#gridChannelNode')[0].updateDeviceEditBtn();
        });


        //当添加一个新设备后，需要对node进行更新
        this.down('#adminDeviceGrid').store.on('update', function (store, record, operation, eOpts) {
            var channelNodeGrid = Ext.ComponentQuery.query('#gridChannelNode')[0];
            var nodeGrid = channelNodeGrid.down("gridEditBase[master='master']");
            var nodeSelected = nodeGrid.getSelectionModel().selected.items;

            var rec = nodeGrid.store.getById(nodeSelected[0].data.id);
            rec.set('device', record.data.id);
            rec.commit();
        });

    }
})

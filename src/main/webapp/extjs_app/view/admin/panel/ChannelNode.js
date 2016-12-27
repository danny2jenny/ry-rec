/**
 * Created by danny on 16-12-19.
 *
 * Channel 和 Node 的管理
 */

Ext.define('app.view.admin.panel.ChannelNode', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.panel.channelnode',
    layout: 'border',
    icon: 'icon/toolbar/control.png',
    title: '通道和节点',
    itemId: 'gridChannelNode',
    items: [
        /**
         * 主表设置
         */
        {
            xtype: 'gridEditBase',
            region: 'center',
            margins: '0 0 0 0',
            title: '控制器通道',
            icon: 'icon/toolbar/channel.png',
            // 定义 Model
            store: 'admin.Channel',
            // 定义 autoload
            autoload: false,
            // 主表id
            itemId: 'master',
            // 定义 colums
            columns: [
                {
                    text: 'ID',
                    dataIndex: 'id',
                    width: 30
                },
                {
                    text: '名称',
                    dataIndex: 'name',
                    width: 300,
                    editor: {
                        allowBlank: false
                    }
                },
                {
                    text: 'IP地址',
                    dataIndex: 'ip',
                    width: 300,
                    editor: {
                        allowBlank: false
                    }
                },
                {
                    text: '端口',
                    dataIndex: 'port',
                    width: 50,
                    editor: {
                        allowBlank: false
                    }
                },
                {
                    text: '登录名',
                    dataIndex: 'login',
                    width: 100,
                    editor: {
                        allowBlank: true
                    }
                },
                {
                    text: '密码',
                    dataIndex: 'pass',
                    width: 100,
                    editor: {
                        allowBlank: true
                    }
                },

                {
                    text: '类型',
                    dataIndex: 'type',
                    width: 200,
                    editor: {
                        xtype: 'combobox',
                        store: ry.CHANNEL_TYPE,
                    },
                    renderer: function (val, column, row) {
                        return ry.trans(val, ry.CHANNEL_TYPE);
                    }
                },

                {
                    text: '配置信息',
                    dataIndex: 'other',
                    width: 300,
                    editor: {
                        allowBlank: true
                    }
                }
            ]
        },

        /**
         * 子表设置
         */
        {
            xtype: 'gridEditBase',
            region: 'south',
            height: 500,
            margins: '0 0 5 0',
            split: true,
            title: '节点',
            icon: 'icon/toolbar/node.png',
            // 定义 Model
            store: 'admin.Node',
            // 定义 autoload
            autoload: false,
            // 主表id
            master: 'master',
            // 定义 外键colums
            fkey: 'cid',
            columns: [{
                text: 'ID',
                dataIndex: 'id'
            }, {
                text: '地址',
                dataIndex: 'adr',
                width: 100,
                editor: {
                    allowBlank: false
                }
            }, {
                text: '端口',
                dataIndex: 'no',
                width: 100,
                editor: {
                    allowBlank: false
                }
            }, {
                text: '名称',
                dataIndex: 'name',
                width: 100,
                editor: {
                    allowBlank: false
                }
            }, {
                text: '节点类型',
                dataIndex: 'type',
                width: 100,
                editor: {
                    xtype: 'combobox',
                    store: ry.NODE_TYPE
                },
                renderer: function (val, column, row) {
                    return ry.trans(val, ry.NODE_TYPE);
                }
            }, {
                text: '端口功能',
                dataIndex: 'devicefun',
                editor: {
                    xtype: 'combobox',
                    store: ry.DEVICE_FUN
                },
                renderer: function (val, column, row) {
                    return ry.trans(val, ry.DEVICE_FUN);
                }
            }, {
                text: '是否关联',
                dataIndex: 'device',
                width: 80,
                renderer: function (val, column, row) {
                    if (val) {
                        return "<img src='/icon/true.png'>"
                    } else {
                        return "<img src='/icon/false.png'>"
                    }
                }
            }, {
                text: '其他配置',
                xtype: 'actioncolumn',
                width: 80,
                items: [{
                    icon: '/icon/config.png', // Use a URL in the icon config
                    tooltip: 'Edit',
                    handler: function (grid, rowIndex, colIndex) {
                        debugger;
                    }
                }]
            }],

            //拖放
            viewConfig: {
                plugins: {
                    ddGroup: 'ddg_node',
                    ptype: 'gridDragPlugin',
                    showField: 'name',
                    enableDrop: false
                }
            }
        }
    ],


    updateDeviceEditBtn: function () {

        var channelNodeGrid = Ext.ComponentQuery.query('#gridChannelNode')[0];
        var nodeGrid = channelNodeGrid.down("gridEditBase[master='master']");
        var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
        var nodeSelected = nodeGrid.getSelectionModel().selected.items;
        var deviceSelected = deviceGrid.getSelectionModel().selected.items;

        if (nodeSelected.length) {

            if (nodeGrid.store.getById(nodeSelected[0].get('id')).get('device') &&
                deviceGrid.store.getById(nodeGrid.store.getById(nodeSelected[0].get('id')).get('device'))) {
                // 有关联的设备
                // 当前Node是否已经分配了Device
                // 如果分配：删除使能，添加不使能，追加不使能
                // 如果没有分配：

                // 得到当前Node对应的Device

                debugger;
                var device = deviceGrid.store.getById(nodeGrid.store.getById(nodeSelected[0].get('id')).get('device')).data;

                // 使能 Delete
                channelNodeGrid.bt_del.setDisabled(false);
                channelNodeGrid.bt_new.setDisabled(true);
                channelNodeGrid.bt_append.setDisabled(true);
                // 更新提示
                channelNodeGrid.bt_note.setText('<b>已经关联设备：' + device.name + '(' + ry.trans(device.type, ry.DEVICE_TYPE) + ')</b>');
            } else {
                // 没有关联的设备
                channelNodeGrid.bt_note.setText("<div style='color: #ff0000'>未关联设备！</div>");
                channelNodeGrid.bt_del.setDisabled(true);
                channelNodeGrid.bt_new.setDisabled(false);
                channelNodeGrid.bt_append.setDisabled(!deviceSelected.length);
            }

        } else {
            channelNodeGrid.bt_new.setDisabled(true);
            channelNodeGrid.bt_del.setDisabled(true);
            channelNodeGrid.bt_append.setDisabled(true);
        }

    },

    /**
     * 初始化
     */
    initComponent: function () {

        var me = this;

        me.callParent(arguments);

        // Node 的 grid
        var nodeGrid = this.down("gridEditBase[master='master']");


        // Node 的工具条
        var tb = nodeGrid.down('toolbar');

        //工具条
        tb.add(Ext.create('Ext.toolbar.Fill'));


        //提示
        me.bt_note = Ext.create('Ext.toolbar.TextItem', {
            text: '选中的节点配置',
            itemId: 'bt_note'
        });

        // 添加
        me.bt_new = Ext.create('Ext.Button', {
            text: '添加为新设备',
            icon: '/icon/toolbar/add.png',
            disabled: true,
            itemId: 'bt_new',
            handler: function (btn, event) {
                var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
                var channelNodeGrid = Ext.ComponentQuery.query('#gridChannelNode')[0];
                var nodeGrid = channelNodeGrid.down("gridEditBase[master='master']");
                var nodeSelected = nodeGrid.getSelectionModel().selected.items;
                deviceGrid.store.insert(0, {
                    name: nodeSelected[0].data.name,
                    type: 0
                });
            }
        });

        // 追加
        me.bt_append = Ext.create('Ext.Button', {
            text: '追加到选中设备',
            icon: '/icon/append.png',
            disabled: true,
            itemId: 'bt_append',
            handler: function (btn, event) {
                var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
                var channelNodeGrid = Ext.ComponentQuery.query('#gridChannelNode')[0];
                var nodeGrid = channelNodeGrid.down("gridEditBase[master='master']");
                var nodeSelected = nodeGrid.getSelectionModel().selected.items;
                nodeSelected[0].set('device', deviceGrid.getSelectionModel().selected.items[0].get('id'));
            }
        });

        // 删除
        me.bt_del = Ext.create('Ext.Button', {
            text: '从设备中删除',
            icon: '/icon/toolbar/delete.png',
            disabled: true,
            itemId: 'bt_del',
            handler: function (btn, event) {
                var channelNodeGrid = Ext.ComponentQuery.query('#gridChannelNode')[0];
                var nodeGrid = channelNodeGrid.down("gridEditBase[master='master']");
                var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
                var nodeSelected = nodeGrid.getSelectionModel();
                deviceGrid.store.remove(deviceGrid.store.getById(nodeGrid.store.getById(nodeSelected.getSelection()[0].get('id')).get('device')));
            }
        });

        tb.add(me.bt_note);
        tb.add(me.bt_new);
        tb.add(me.bt_append);
        tb.add(me.bt_del);

        //当记录选中改变的时候
        /**
         * 如果记录有相应的
         */
        nodeGrid.on('selectionchange', function (view, records) {
            this.ownerCt.updateDeviceEditBtn();
        });

        nodeGrid.store.on('update', function (store, record, operation, eOpts) {
            var channelNodeGrid = Ext.ComponentQuery.query('#gridChannelNode')[0];
            channelNodeGrid.updateDeviceEditBtn();
        });

        nodeGrid.view.on('refresh', function (view, eOpts) {
            var channelNodeGrid = Ext.ComponentQuery.query('#gridChannelNode')[0];
            channelNodeGrid.updateDeviceEditBtn();
        });

        // 当数据改变成功后的回调
        Ext.direct.Manager.on('event', function (event, provider, eOpts) {
            // event.method

            // 当 Device 删除后，应该刷新 Node
            if (event.action == 'extDevice' && event.method == 'delete') {
                Ext.StoreManager.get('admin.Node').reload();
            }

        });

    }


});
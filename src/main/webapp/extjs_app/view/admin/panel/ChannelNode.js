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
            itemId: 'adminNodeGridForChannel',
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


        //更新成功后
        this.down('#adminNodeGridForChannel').store.on('update', function (store, record, operation, eOpts) {
            var nodeGrid = Ext.ComponentQuery.query('#adminNodeGridForDevice')[0];
            nodeGrid.store.load();
        })

        // 当数据改变成功后的回调
        Ext.direct.Manager.on('event', function (event, provider, eOpts) {
            // 当 Device 删除后，应该刷新 Node
            if (event.action == 'extDevice' && event.method == 'delete') {
            }

        });

    }


});
/**
 * Created by danny on 16-12-19.
 *
 * Channel 和 Node 的管理
 */

Ext.define('ryrec.view.admin.panel.ChannelNode', {
    requires: ['ryrec.lib.MasterSlaveGride'],
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.panel.channelnode',
    layout: 'border',
    icon: 'icon/toolbar/control.png',
    title: '设备编辑',
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
                    width: 100,
                    editor: {
                        allowBlank: false
                    }
                },
                {
                    text: '配置信息',
                    dataIndex: 'other',
                    width: 300,
                    editor: {
                        allowBlank: true
                    }
                }]
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
            columns: [
                {
                    text: 'ID',
                    dataIndex: 'id'
                },
                {
                    text: '地址',
                    dataIndex: 'adr',
                    width: 100,
                    editor: {
                        allowBlank: false
                    }
                },
                {
                    text: '端口',
                    dataIndex: 'no',
                    width: 100,
                    editor: {
                        allowBlank: false
                    }
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
                    text: '类型',
                    dataIndex: 'type',
                    width: 100,
                    editor: {
                        allowBlank: false
                    }
                },
                {
                    text: '配置',
                    dataIndex: 'other',
                    width: 100,
                    editor: {
                        allowBlank: false
                    }
                }]
        }
    ]

});


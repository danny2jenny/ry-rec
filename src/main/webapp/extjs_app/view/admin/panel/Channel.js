/**
 * Created by danny on 17-1-4.
 *
 * Channel 列表
 */


Ext.define('app.view.admin.panel.Channel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.channel',
    title: '控制器通道',
    icon: 'icon/toolbar/channel.png',

    store: 'Channel',
    itemId: 'adminChannelGrid',

    plugins: [{
        ptype: 'grid.editing',
        autoLoad: true,
    }],

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
            dataIndex: 'opt',
            width: 300,
            editor: {
                allowBlank: true
            }
        }
    ]
});

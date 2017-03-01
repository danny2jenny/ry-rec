/**
 * Created by danny on 16-12-19.
 *
 * Channel 和 Node 的管理
 */

Ext.define('app.view.admin.frame.ChannelNode', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.channelnode',
    layout: 'border',
    icon: 'icon/toolbar/control.png',
    title: '通道和节点',
    itemId: 'gridChannelNode',
    items: [
        /**
         * 主表设置
         */
        {
            xtype: 'admin.panel.channel',
            region: 'center',
            margins: '0 0 0 0',
        },

        {
            xtype: 'panel',
            region: 'south',
            height: 500,
            margins: '0 0 5 0',
            layout: 'border',
            items: [
                {
                    xtype: 'admin.panel.node',
                    region: 'center',
                },
                {
                    xtype: 'admin.panel.nodeconfig',
                    region: 'east',
                    width: 400
                }
            ]
        }

    ],


    /**
     * 初始化
     */
    initComponent: function () {

        var me = this;

        me.callParent(arguments);

        // 当数据改变成功后的回调
        Ext.direct.Manager.on('event', function (event, provider, eOpts) {

            var nodeForDevice = Ext.ComponentQuery.query('#admin_panel_NodeForDevice')[0];
            var nodeForChannel = Ext.ComponentQuery.query('#adminNodeGridForChannel')[0];

            // 当Channel删除后，应该更新 NodeForDevice 和 NodeForChannel
            if (event.action == 'extChannel' && event.method == 'delete') {
                nodeForChannel.editPlugin.reload();
                nodeForDevice.editPlugin.reload();
            }


            // 当Node删除后，应该刷新 NodeForDevice
            if (event.action == 'extNode' && event.method == 'delete') {
                nodeForDevice.editPlugin.reload();
            }

            // 当Node更新后，应该刷新 NodeForDevice
            if (event.action == 'extNode' && event.method == 'update') {
                nodeForDevice.editPlugin.reload();
            }

        });

        this.on('show', function (from, eOpts) {
            Ext.ComponentQuery.query('#griddeviceNode')[0].hideAllSouthPanel();
            var node = Ext.ComponentQuery.query('#admin_panel_NodeForDevice')[0];
            node.show();
        })
    }
});
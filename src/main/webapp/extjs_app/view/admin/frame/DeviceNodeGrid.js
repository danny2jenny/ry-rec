/**
 * Created by danny on 16-12-26.
 *
 * Device 和 Node 的编辑
 */

Ext.define('app.view.admin.frame.DeviceNodeGrid', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.devicenodegrid',
    layout: 'border',
    itemId: 'griddeviceNode',

    requires: [
        'app.lib.PluginGridEdit'
    ],

    items: [
        {
            xtype: 'admin.panel.device',
            region: 'center'
        },

        // 子表设置
        {
            xtype: 'admin.panel.nodefordevice',
            region: 'south',
            height: 300,
            hidden: false,
            //margins: '0 0 5 0',
            split: true,
        },

        {
            // 一个Device的Gis配置信息
            xtype: 'admin.panel.gis',
            region: 'south',
            height: 300,
            hidden: true,
        }
    ],


    // 隐藏所有的子面板
    hideAllSouthPanel: function () {

        var panels = this.query('panel[region=south]');
        for (var index in panels) {
            var panel = panels[index];
            panel.hide();
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        //Device 的拖放
        /*
         * 当 NodeForChannel 拖放到DeviceGrid这个Panel中的时候
         * 以 NodeForChannel 的名字来建立一个Device记录
         */
        this.down('#adminDeviceGrid').on('onDragDrop', function (data, targetNode, position) {
            if (!data.records.length) {
                return;
            }

            var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];

            var newItem = deviceGrid.store.insert(0, {
                name: data.records[0].get('name'),
                type: 0
            })

        });

        /*
         * 当NodeForChannel拖放到NodeForDevice中的时候
         * 判断Device是否选择
         * 以Device的Id来更新NodeForChannel的device字段
         */
        this.down('#admin_panel_NodeForDevice').on('onDragDrop', function (data, targetNode, position) {
            var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
            var nodeGrid = Ext.ComponentQuery.query('#admin_panel_NodeForDevice')[0];

            if (!deviceGrid.getSelectionModel().selected.length) {
                return
            }


            var deviceId = deviceGrid.getSelectionModel().selected.items[0].get('id');

            var updateItem = data.records[0];
            updateItem.set('device', deviceId);
            updateItem.commit();
        });

        /*
         * 覆盖Node的Delete方法
         * 只是设置相应的 Node 的 device 为 0
         */
        var nodeForDevice = Ext.ComponentQuery.query('#admin_panel_NodeForDevice')[0];

        nodeForDevice.down('#buttonDelete').handler = function () {
            var node = nodeForDevice.getSelectionModel().selected.get(0);
            node.set('device', 0);
            node.commit();
        };

        Ext.direct.Manager.on('event', function (event, provider, eOpts) {

            var nodeForDevice = Ext.ComponentQuery.query('#admin_panel_NodeForDevice')[0];
            var nodeForChannel = Ext.ComponentQuery.query('#adminNodeGridForChannel')[0];

            // 当 Device 删除后，应该刷新 NodeForChannel NodeForDevice
            if (event.action == 'extDevice' && event.method == 'delete') {
                nodeForChannel.editPlugin.reload();
                nodeForDevice.editPlugin.reload();
            }

            // 当 NodeForDevice，更新，应该更新 NodeForChannel
            if (event.action == 'extNodeForDevice' && event.method == 'update') {
                nodeForChannel.editPlugin.reload();
            }

        }, this);

    }
});

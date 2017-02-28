/**
 * Created by danny on 16-12-26.
 *
 * Device 和 Node 的编辑
 */

Ext.define('app.view.admin.frame.DeviceNodeGrid', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.devicenodegrid',
    layout: 'border',
    // icon: 'icon/toolbar/control.png',
    // title: '设备编辑',
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
        this.down('#adminDeviceGrid').on('onDragDrop', function (data, targetNode, position) {
            if (!data.records.length) {
                return;
            }

            var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];

            var newItem = deviceGrid.store.insert(1, {
                name: data.records[0].get('name'),
                type: 0
            })
        });

        //Node 的拖放
        this.down('#admin_panel_NodeForDevice').on('onDragDrop', function (data, targetNode, position) {
            debugger;
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

        // 覆盖Node的Delete方法
        // 只是设置相应的 Node 的 device 为 0

        var nodeGrid = Ext.ComponentQuery.query('#admin_panel_NodeForDevice')[0];

        nodeGrid.down('#buttonDelete').handler = function () {
            var node = nodeGrid.getSelectionModel().selected.get(0);
            node.set('device', 0);
        };

        nodeGrid.store.on('update', function (store, record, operation, eOpts) {
            Ext.ComponentQuery.query('#admin_panel_NodeForDevice')[0].store.load();
            Ext.ComponentQuery.query('#adminNodeGridForChannel')[0].store.load();
        })
    }
})

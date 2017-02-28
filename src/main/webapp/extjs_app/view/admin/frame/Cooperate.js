/**
 * Created by danny on 17-2-16.
 */
Ext.define('app.view.admin.frame.Cooperate', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.cooperate',
    layout: 'border',
    icon: '/icon/toolbar/gis.png',
    title: '联动配置',
    items: [

        {
            xtype: 'admin.panel.ActionRule',
            region: 'center',
            margins: '0 0 0 0'
        },
        {
            xtype: 'admin.panel.Actions',
            margins: '0 0 5 0',
            region: 'south',
            height: 500,
        }
    ],

    // device选择改变
    onDeviceSelectChange: function (view, selections, options) {
        // 如果当前的Panel没有激活，退出
        if (this.hidden) {
            return;
        }

        var deviceFrame = Ext.ComponentQuery.query('#griddeviceNode')[0];
        deviceFrame.hideAllSouthPanel();
        if (selections.length) {
            debugger;
            var device = ry.devices['device_' + selections[0].data.type];
            if (device.configPanel) {
                var configPanel = device.configPanel;
                configPanel.readConfig(selections[0], 'opt');
            }
        }
    },

    // 初始化
    initComponent: function () {
        this.callParent(arguments);

        //把ry.devices中的Panel加入到这里
        var deviceFrame = Ext.ComponentQuery.query('#griddeviceNode')[0];
        deviceFrame.down('#adminDeviceGrid').on('selectionchange', this.onDeviceSelectChange, this);

        for (var deviceMgrIndex in ry.devices) {
            var deviceMgr = ry.devices[deviceMgrIndex];
            if (deviceMgr.configPanel) {
                deviceMgr.configPanel.region = 'south';
                deviceFrame.add(deviceMgr.configPanel);
            }
        }

        this.on('show', function (from, eOpts) {
            var deviceFrame = Ext.ComponentQuery.query('#griddeviceNode')[0];
            deviceFrame.hideAllSouthPanel();
        })

    }
});
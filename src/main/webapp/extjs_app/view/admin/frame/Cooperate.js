/**
 * Created by danny on 17-2-16.
 */
Ext.define('app.view.admin.frame.Cooperate', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.cooperate',
    layout: 'border',
    icon: 'res/toolbar/gis.png',
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


        var deviceFrame = Ext.ComponentQuery.query('#griddeviceNode')[0];
        deviceFrame.down('#adminDeviceGrid').on('selectionchange', this.onDeviceSelectChange, this);

        //把ry.devices中的Panel加入到这里
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
        });

        // 当Device删除后需要做的工作
        Ext.direct.Manager.on('event', function (event, provider, eOpts) {

            var actionRules = Ext.ComponentQuery.query('#admin_panel_action_rule')[0];
            var actions = Ext.ComponentQuery.query('#admin_panel_actions')[0];

            // 当 ActionRule 删除后，应该刷新 Actions
            if (event.action == 'extActionRule' && event.method == 'delete') {
                actions.editPlugin.reload();
            }

            // 当 Device删除后，应该更新 Actions 和 ActionRule
            if (event.action == 'extDevice' && event.method == 'delete') {
                actions.editPlugin.reload();
                actionRules.editPlugin.reload();
            }

        }, this);

    }
});
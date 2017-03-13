/**
 * Created by Danny on 2017/3/4.
 *
 * 所有Device 的控制面板的管理
 */

Ext.define('app.view.device.DevicePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.device.devicepanel',
    layout: 'fit',
    width: 230,
    autoDestroy: false,


    // 隐藏所有的ControlPanel
    hideAll: function () {
        var panels = this.query("panel");
        for (var i = 0; i < panels.length; i++) {
            try {
                panels[i].hide();
            } catch (error) {

            }

        }
    },

    // 判断是否有相应的controlPanel
    hasPanel: function (type) {
        var panel = ry.devices['device_' + type].controlPanel;
        if (panel) {
            return true;
        } else {
            return false;
        }
    },

    // 显示Type相符合的Controlpanel
    // 返回true成表示有这个Controlpanel，否则返回false
    showPanel: function (state) {
        this.show();
        this.hideAll();
        var panel = ry.devices['device_' + state.device.type].controlPanel;
        panel.show();

        // 设置当前显示的Device
        ry.devices['device_' + state.device.type].controlPanel.updateState(state);
    },

    initComponent: function () {
        this.callParent(arguments);

        // 把所有的controlPanel 进行加载
        for (var i in ry.devices) {
            var deviceMgr = ry.devices[i];
            if (deviceMgr.controlPanel) {
                this.add(deviceMgr.controlPanel);
            }
        }

    }
});

ry.deviceControlPanel = Ext.create('app.view.device.DevicePanel', {});
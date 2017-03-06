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
    showPanel: function (type, state) {
        this.show();
        this.removeAll();
        this.add(ry.devices['device_' + type].controlPanel);
        // 设置当前显示的Device
        this.deviceId = state.device.id;
        ry.devices['device_' + type].controlPanel.updateState(state);
    },

    initComponent: function () {
        this.callParent(arguments);
    }
});

ry.deviceControlPanel = Ext.create('app.view.device.DevicePanel', {});
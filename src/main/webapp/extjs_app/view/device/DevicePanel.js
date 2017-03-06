/**
 * Created by Danny on 2017/3/4.
 *
 * 所有Device 的控制面板的管理
 */

Ext.define('app.view.device.DevicePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.device.devicepanel',
    layout: 'fit',
    width: 200,

    // 隐藏所有的ControlPanel
    hideAll: function () {
        debugger;
        var panels = this.query("panel");
        for (var i = 0; i < panels.length; i++) {
            panels[i].hide();
        }
    },

    // 显示Type相符合的Controlpanel
    // 返回true成表示有这个Controlpanel，否则返回false
    showPanel: function (type) {
        this.hideAll();
        var panel = Ext.getCmp('device.controlpanel.' + type);
        if (panel) {
            panel.show();
            return true;
        } else {
            return false;
        }
    },

    initComponent: function () {
        this.callParent(arguments);
        for (var i in ry.devices) {
            var deviceMgr = ry.devices[i];
            if (deviceMgr.controlPanel) {
                this.add(deviceMgr.controlPanel);
            }
        }
    }
});

ry.deviceControlPanel = Ext.create('app.view.device.DevicePanel', {});
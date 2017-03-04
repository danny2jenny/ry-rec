/**
 * Created by danny on 17-2-16.
 *
 * 空调控制
 */

Ext.define('app.device.Device_301', {
    //没有配置面板
});

ry.devices['device_301'] = Ext.create('app.device.Device_301', {});

var testPanel = Ext.create('Ext.panel.Panel', {
    bodyPadding: 5,  // Don't want content to crunch against the borders
    width: 300,
    title: 'Filters',
    items: [{
        xtype: 'datefield',
        fieldLabel: 'Start date'
    }, {
        xtype: 'datefield',
        fieldLabel: 'End date'
    }]
});


/**
 * Created by danny on 17-2-16.
 *
 * 开关控制
 */

// 配置面板
Ext.define('app.device.Device_101', {
    //没有配置面板
});

ry.devices['device_101'] = Ext.create('app.device.Device_101', {});

ry.deviceEditor['act_101'] = Ext.create('Ext.form.field.ComboBox', {
    forceSelection: true,
    allowBlank: false,
    editable: false,
    autoSelect: true,
    triggerAction: 'all',
    store: ry.DEVICE_ACT_101
});
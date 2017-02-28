/**
 * Created by danny on 17-2-16.
 *
 * 开关输入
 */

// 配置面板
Ext.define('app.device.Device_102', {
    // 没有配置面板
});

ry.devices['device_102'] = Ext.create('app.device.Device_102', {});

ry.deviceEditor['sig_102'] = Ext.create('Ext.form.field.ComboBox', {
    forceSelection: true,
    allowBlank: false,
    editable: false,
    autoSelect: true,
    triggerAction: 'all',
    store: ry.DEVICE_SIG_102
});


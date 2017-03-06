/**
 * Created by danny on 17-2-16.
 *
 * 开关控制
 */

// 配置面板
Ext.define('app.device.Device_101', {
    //没有配置面板

    // 开关控制面板
    controlPanel: Ext.create('Ext.panel.Panel', {
        id: 'device.controlpanel.101',
        bodyPadding: 5,
        width: 200,
        //hideen: true,
        items: [{
            xtype: 'textfield',
            itemId: 'output',
            disabled: true,
            fieldLabel: '状态',
            value: ""
        }, {
            xtype: 'textfield',
            itemId: 'remote',
            disabled: true,
            fieldLabel: '工作模式',
            value: ""
        }, {
            xtype: 'textfield',
            itemId: 'feedback',
            disabled: true,
            fieldLabel: '反馈状态',
            value: ""
        }]
    })
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
/**
 * Created by danny on 17-2-16.
 *
 * 开关输入
 */

// 配置面板
Ext.define('app.device.Device_102', {
    // 没有配置面板
    // 控制面板
    controlPanel: Ext.create('Ext.panel.Panel', {
        id: 'device.controlpanel.102',
        bodyPadding: 5,
        autoDestroy: false,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },

        items: [{
            xtype: 'textfield',
            itemId: 'output',
            readOnly: true,
            fieldLabel: '状态',
            value: ""
        }],

        /**
         * 每个控制面板都应该实现
         * 服务器数据更新后的刷新
         * @param state
         */
        refreshState: function (state) {


        },

        /**
         * 读取客户端的状态数据，并显示
         * 每个面板都应该实现
         * @param state
         */
        updateState: function (state) {

        }
    })
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


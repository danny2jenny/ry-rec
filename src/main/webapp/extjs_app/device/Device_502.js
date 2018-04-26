// 控制面板
Ext.define('app.view.device.control._502', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.device.control.502',
    title: '模拟量监测',
    width: 200,

    bodyPadding: 5,
    autoDestroy: false,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'textfield',
            itemId: 'type',
            readOnly: true,
            fieldLabel: '告警类型',
            value: ""
        }, {
            xtype: 'textfield',
            itemId: 'position',
            readOnly: true,
            fieldLabel: '告警位置',
            value: ""
        }, {
            xtype: 'textfield',
            itemId: 'val',
            readOnly: true,
            fieldLabel: '告警温度',
            value: ""
        }
    ],

    /**
     * 每个控制面板都应该实现
     * 服务器数据更新后的刷新
     * @param state
     */
    refreshState: function (state) {

        if (!this.runtime) {
            return;
        }

        if (this.runtime.device != state.device.id) {
            return
        }

        this.updateState(state);
    },

    /**
     * 读取客户端的状态数据，并显示
     * 每个面板都应该实现
     * @param state
     */
    updateState: function (state) {
        // 不是自己的数据
        if (state.device.type != 502) {
            return;
        }

        this.runtime = state.runtime;

        this.setTitle("光纤测温：" + state.device.name);


        this.down("#type").setValue(ry.trans(state.runtime.state.type, ry['DEVICE_SIG_502']));
        this.down("#position").setValue(state.runtime.state.position + '米');
        this.down("#val").setValue(state.runtime.state.val + '度');

    }
});

ry.devices['device_502'] = {};


ry.deviceEditor['sig_502'] = Ext.create('Ext.form.field.ComboBox', {
    forceSelection: true,
    allowBlank: false,
    editable: false,
    autoSelect: true,
    triggerAction: 'all',
    store: ry.DEVICE_SIG_502
});
/**
 * Created by danny on 17-2-16.
 *
 * 开关控制
 */

// 设备控制面板
Ext.define('app.view.device.control._101', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.device.control.101',
    title: '开关控制',
    //id: 'device.control.101',
    width: 200,

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
    }, {
        xtype: 'textfield',
        itemId: 'remote',
        readOnly: true,
        fieldLabel: '工作模式',
        value: ""
    }, {
        xtype: 'textfield',
        itemId: 'feedback',
        readOnly: true,
        fieldLabel: '反馈状态',
        value: ""
    }, {
        xtype: 'button',
        itemId: 'button',
        text: 'Click me',
        handler: function () {
            if (this.ownerCt.runtime.state.output == 20) {
                // 开启
                action.operate(this.ownerCt.runtime.device, 101, null);
            } else {
                // 关闭
                action.operate(this.ownerCt.runtime.device, 100, null);
            }
        }
    }],

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
        if (state.device.type != 101) {
            return;
        }

        this.runtime = state.runtime;

        this.setTitle("开关控制：" + state.device.name);

        this.down("#button").enable();
        // 开关状态
        if (this.runtime.state.output == 20) {
            this.down("#output").setValue("关闭");
            this.down("#button").setText("点击开启")
        }
        if (this.runtime.state.output == 21) {
            this.down("#output").setValue("开启");
            this.down("#button").setText("点击关闭")
        }
        if (this.runtime.state.output == null) {
            this.down("#output").setValue("失效！");
            this.down("#button").setText("失效！");
            this.down("#button").disable();
        }

        // 远程、就地状态
        if (this.runtime.state.remote == 20) {
            this.down("#remote").setValue("远程控制")
        }
        if (this.runtime.state.remote == 21) {
            this.down("#remote").setValue("就地控制")
        }
        if (this.runtime.state.remote == null) {
            this.down("#remote").setValue("未配置/失效！")
        }

        // 反馈状态
        if (this.runtime.state.feedback == 20) {
            this.down("#feedback").setValue("关闭")
        }
        if (this.runtime.state.feedback == 21) {
            this.down("#feedback").setValue("开启")
        }
        if (this.runtime.state.feedback == null) {
            this.down("#feedback").setValue("未配置/失效！")
        }

    }
});

// 必须有
ry.devices['device_101'] = {};

// 设备动作
ry.deviceEditor['act_101'] = Ext.create('Ext.form.field.ComboBox', {
    forceSelection: true,
    allowBlank: false,
    editable: false,
    autoSelect: true,
    triggerAction: 'all',
    store: ry.DEVICE_ACT_101
});
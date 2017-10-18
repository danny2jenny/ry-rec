/**
 * Created by danny on 17-2-16.
 *
 * 空调控制
 */

// 设备控制面板
Ext.define('app.view.device.control._301', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.device.control.301',
    title: '空调控制',
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
            itemId: 'state',
            readOnly: true,
            fieldLabel: '工作状态',
            value: ""
        }, {
            xtype: 'button',
            itemId: 'off',
            text: '关闭',
            handler: function () {
                action.operate(this.ownerCt.runtime.device, 1, 1);
            }
        }, {
            xtype: 'button',
            itemId: 'cold',
            text: '制冷',
            handler: function () {
                action.operate(this.ownerCt.runtime.device, 2, 2);
            }
        }, {
            xtype: 'button',
            itemId: 'hot',
            text: '制热',
            handler: function () {
                action.operate(this.ownerCt.runtime.device, 3, 3);
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
        if (state.device.type != 301) {
            return;
        }

        this.runtime = state.runtime;

        this.setTitle("空调控制：" + state.device.name);

        // 空调状态
        if (this.runtime.state.value == null) {
            this.down("#state").setValue("!无数据!");
        }
        if (this.runtime.state.value == 0) {
            this.down("#state").setValue("关闭");
        }
        if (this.runtime.state.value == 1) {
            this.down("#state").setValue("制冷");
        }
        if (this.runtime.state.value == 2) {
            this.down("#state").setValue("制热");
        }
    }
});

ry.devices['device_301'] = {};

// 设备动作
ry.deviceEditor['act_301'] = Ext.create('Ext.form.field.ComboBox', {
    forceSelection: true,
    allowBlank: false,
    editable: false,
    autoSelect: true,
    triggerAction: 'all',
    store: ry.DEVICE_ACT_301
});

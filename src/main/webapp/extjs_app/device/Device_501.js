// 控制面板
Ext.define('app.view.device.control._501', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.device.control.501',
    title: '环流监测',
    dType: 501,     // 设备类型
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
            itemId: 'vA',
            readOnly: true,
            fieldLabel: 'A相电流',
            value: ""
        }, {
            xtype: 'textfield',
            itemId: 'vB',
            readOnly: true,
            fieldLabel: 'B相电流',
            value: ""
        }, {
            xtype: 'textfield',
            itemId: 'vC',
            readOnly: true,
            fieldLabel: 'C相电流',
            value: ""
        }, {
            xtype: 'textfield',
            itemId: 'vO',
            readOnly: true,
            fieldLabel: '0序电流',
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
        if (state.device.type != 501) {
            return;
        }

        this.runtime = state.runtime;

        this.setTitle("电流监测：" + state.device.name);

        this.down("#vA").setValue(state.runtime.state.A.toFixed(1) + 'A');
        this.down("#vB").setValue(state.runtime.state.B.toFixed(1) + 'A');
        this.down("#vC").setValue(state.runtime.state.C.toFixed(1) + 'A');
        this.down("#vO").setValue(state.runtime.state.O.toFixed(1) + 'A');

    }
});

ry.devices['device_501'] = {};
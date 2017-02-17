/**
 * Created by danny on 17-2-16.
 *
 * 联动规则
 */


Ext.define('app.view.admin.panel.ActionRule', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.ActionRule',
    title: '联动规则',
    icon: 'icon/toolbar/channel.png',

    store: 'ActionRule',
    itemId: 'admin_panel_action_rule',

    plugins: [{
        ptype: 'grid.editing',
        isCell: true,
        hideAdd: true,
        autoLoad: true,
    }],

    // plugins: [
    //     Ext.create('Ext.grid.plugin.CellEditing', {
    //         clicksToEdit: 1
    //     })
    // ],

    columns: [
        {
            text: 'ID',
            dataIndex: 'id',
            width: 30
        },
        {
            text: '关联设备',
            dataIndex: 'device',
            width: 300,
            renderer: function (val, column, row) {
                var store = Ext.getStore('Device');
                return store.getById(val).getData().name;
            }
        },

        {
            text: '信号',
            itemId: 'fieldSig',
            dataIndex: 'sig',
            width: 200,
            editor: ry.deviceEditor.sig_102,
            getEditor: function (record, defaultField) {
                debugger;
                var rec = record.getData();
                var device = Ext.getStore('Device').getById(rec.device).getData();

                var editor = Ext.create('Ext.grid.CellEditor', {
                    field: ry.deviceEditor['sig_' + device.type]
                })

                return editor;
            },
            renderer: function (val, column, row) {
                var data = Ext.getStore('Device').getById(column.record.data.device).getData();
                return ry.trans(val, ry['DEVICE_SIG_' + data.type]);
            }
        }
    ],

    // 主表选择改变的事件
    onSelectChange: function (view, selections, options) {
        debugger;
        var device = selections[0].getData().device;
        var data = Ext.getStore('Device').getById(device).getData();
        this.down('#fieldSig').setEditor(ry.deviceEditor['act_' + data.type]);
    },

    /**
     * 初始化
     */
    initComponent: function () {
        var me = this;
        me.callParent(arguments);

        //me.on('selectionchange', me.onSelectChange, this);
    }
});
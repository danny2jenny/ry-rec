/**
 * Created by danny on 17-2-16.
 * 联动动作
 */


Ext.define('app.view.admin.panel.Actions', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.Actions',
    title: '规则动作',
    icon: 'res/toolbar/action.png',

    store: 'Actions',
    itemId: 'admin_panel_actions',

    plugins: [{
        ptype: 'grid.editing',
        isCell: true,
        hideAdd: true,
        autoLoad: false,
        masterGrid: 'admin_panel_action_rule',
        fKey: 'rule',
        newRec: {}          //新建记录的缺省值
    }],

    columns: [
        {
            text: 'ID',
            dataIndex: 'id',
            width: 30
        },
        {
            text: '关联设备',
            dataIndex: 'target',
            flex: 1,
            renderer: function (val, column, row) {
                var store = Ext.getStore('Device');
                return store.getById(val).getData().name;
            }
        }, {
            text: '动作',
            itemId: 'fieldAct',
            dataIndex: 'act',
            width: 200,
            getEditor: function (record, defaultField) {
                var rec = record.getData();
                var device = Ext.getStore('Device').getById(rec.target).getData();

                var editor = Ext.create('Ext.grid.CellEditor', {
                    allowBlank: false,
                    field: ry.deviceEditor['act_' + device.type]
                })

                return editor;
            },
            renderer: function (val, column, row) {
                var data = Ext.getStore('Device').getById(column.record.data.target).getData();
                return ry.trans(val, ry['DEVICE_ACT_' + data.type]);
            }
        }, {
            text: '参数',
            itemId: 'fieldParm',
            dataIndex: 'parm',
            width: 200,
            editor: {
                xtype: 'numberfield'
            }
        }
    ],

    viewConfig: {
        plugins: {
            dropGroup: 'groupDevice',
            ptype: 'gridDragPlugin',
            enableDrop: true,
            enableDrag: false
        }
    },

    // 拖放后的事件
    onDrop: function (data, targetRule, position) {
        if (!data.records.length) {
            return;
        }
        var data = data.records[0].data;

        if (!ry.deviceEditor['act_' + data.type]) {
            return;
        }

        this.newRec.target = data.id;
        var newItem = this.store.insert(this.store.getCount(), this.newRec);
    },

    /**
     * 初始化
     */
    initComponent: function () {
        var me = this;
        me.callParent(arguments);
        me.on('onDragDrop', me.onDrop, this);
    }
});

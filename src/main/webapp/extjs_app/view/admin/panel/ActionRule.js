/**
 * Created by danny on 17-2-16.
 *
 * 联动规则
 */


Ext.define('app.view.admin.panel.ActionRule', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.ActionRule',
    title: '联动规则',
    icon: 'res/toolbar/rule.png',

    store: 'ActionRule',
    itemId: 'admin_panel_action_rule',

    plugins: [{
        ptype: 'grid.editing',
        isCell: true,
        hideAdd: true,
        autoLoad: true,
    }],

    // 分组
    features: [Ext.create('Ext.grid.feature.Grouping', {
        groupHeaderTpl: ['{groupValue:this.formatValue}: 共 ({rows.length}) 个', {
            formatValue: function (value) {
                var store = Ext.getStore('Device');
                return store.getById(value).getData().name;
            }
        }]
    })],

    columns: [
        {
            text: 'ID',
            dataIndex: 'id',
            width: 30
        },
        {
            text: '关联设备',
            dataIndex: 'device',
            flex: 1,
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
        //data.view.ownerCt;
        var data = data.records[0].data;

        if(!ry.deviceEditor['sig_'+data.type]){
            return;
        }

        var newItem = this.store.insert(this.store.getCount(), {
            device: data.id
        });
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
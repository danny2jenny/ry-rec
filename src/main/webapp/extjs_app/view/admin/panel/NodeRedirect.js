/**
 * Created by danny on 17-1-4.
 *
 * 用于 属于Node的子表
 */


Ext.define('app.view.admin.panel.NodeRedirect', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.noderedirect',

    itemId: 'adminNodeRedirect',

    split: true,
    title: '需要复用的设备',
    icon: 'res/toolbar/device.png',

    store: 'NodeRedirect',
    plugins: [{
        ptype: 'grid.editing',
        autoLoad: false,
        masterGrid: 'adminAllNode',
        fKey: 'node',
        hideAdd: true,
        newRec: {}          //新建记录的缺省值
    }],

    columns: [{
        text: 'ID',
        dataIndex: 'id'
    }, {
        text: '设备',
        dataIndex: 'device',
        flex: 1,
        renderer: function (val, column, row) {
            return Ext.StoreManager.get('Device').data.get(val).get('name')
        }
    }, {
        text: '端口功能',
        dataIndex: 'devicefun',
        width: 200,
        editor: {
            xtype: 'combobox',
            store: ry.DEVICE_FUN
        },
        renderer: function (val, column, row) {
            return ry.trans(val, ry.DEVICE_FUN);
        }
    }],

    //拖放
    viewConfig: {
        plugins: {
            dropGroup: 'groupDevice',
            ptype: 'gridDragPlugin',
            showField: 'name',
            enableDrop: true
        }
    },

    // 拖放后的事件
    onDrop: function (data, targetRule, position) {
        if (!data.records.length) {
            return;
        }

        //data.view.ownerCt;
        var data = data.records[0].data;
        this.newRec.device = data.id;
        this.newRec.devicefun = 101;
        var newItem = this.store.insert(this.store.getCount(), this.newRec);
    },

    initComponent: function () {
        var me = this;

        me.callParent(arguments);
        me.on('onDragDrop', me.onDrop, this);
    }
});


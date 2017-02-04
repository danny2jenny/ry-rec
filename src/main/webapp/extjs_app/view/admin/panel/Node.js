/**
 * Created by danny on 17-1-4.
 *
 * Node 列表
 */


Ext.define('app.view.admin.panel.Node', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.node',

    itemId: 'adminNodeGridForChannel',

    split: true,
    title: '节点',
    icon: 'icon/toolbar/node.png',

    store: 'Node',
    plugins: [{
        ptype: 'grid.editing',
        autoLoad: false,
        masterGrid: 'adminChannelGrid',
        fKey: 'cid',
        newRec: {}          //新建记录的缺省值
    }],

    columns: [{
        text: 'ID',
        dataIndex: 'id'
    }, {
        text: '地址',
        dataIndex: 'adr',
        width: 100,
        editor: {
            allowBlank: false
        }
    }, {
        text: '端口',
        dataIndex: 'no',
        width: 100,
        editor: {
            allowBlank: false
        }
    }, {
        text: '名称',
        dataIndex: 'name',
        width: 100,
        editor: {
            allowBlank: false
        }
    }, {
        text: '节点类型',
        dataIndex: 'type',
        width: 100,
        editor: {
            xtype: 'combobox',
            store: ry.NODE_TYPE
        },
        renderer: function (val, column, row) {
            return ry.trans(val, ry.NODE_TYPE);
        }
    }, {
        text: '端口功能',
        dataIndex: 'devicefun',
        editor: {
            xtype: 'combobox',
            store: ry.DEVICE_FUN
        },
        renderer: function (val, column, row) {
            return ry.trans(val, ry.DEVICE_FUN);
        }
    }, {
        text: '是否关联',
        dataIndex: 'device',
        width: 80,
        renderer: function (val, column, row) {
            if (val) {
                return "<img src='/icon/true.png'>"
            } else {
                return "<img src='/icon/false.png'>"
            }
        }
    }, {
        text: '其他配置',
        xtype: 'actioncolumn',
        width: 80,
        items: [{
            icon: '/icon/config.png', // Use a URL in the icon config
            tooltip: 'Edit',
            handler: function (grid, rowIndex, colIndex, a, b, c, d, e, f, g) {
                var w = Ext.create('app.view.admin.window.NodeConfig', {});
                w.showAt(b.xy);
                debugger;
            }
        }]
    }],

    //拖放
    viewConfig: {
        plugins: {
            ddGroup: 'ddg_node',
            ptype: 'gridDragPlugin',
            showField: 'name',
            enableDrop: false
        }
    },

    onSelectChange: function (view, selections, options) {
        var panelConfig = Ext.ComponentQuery.query('#admin_panel_nodeconfig')[0];
        if (selections.length) {
            panelConfig.show();
        } else {
            panelConfig.hide();
        }

        debugger
    },

    initComponent: function () {
        var me = this;

        me.callParent(arguments);
        me.on('selectionchange', me.onSelectChange, this);
    }
})
;


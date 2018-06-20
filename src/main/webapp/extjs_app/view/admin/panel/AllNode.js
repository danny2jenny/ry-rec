Ext.define('app.view.admin.panel.AllNode', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.allnode',
    title: '所有节点',
    store: 'NodeAll',
    itemId: 'adminAllNode',
    icon: 'res/toolbar/node.png',

    plugins: [
        {
            ptype: 'grid.editing',
            autoLoad: true,
            hideAdd: true,
            hideDel: true
        }
    ],


    columns: [{
        text: 'ID',
        width: 50,
        dataIndex: 'id'
    }, {
        text: '地址',
        dataIndex: 'adr',
        width: 50,
        editor: {
            allowBlank: false
        }
    }, {
        text: '端口',
        dataIndex: 'no',
        width: 50,
        editor: {
            allowBlank: false
        }
    }, {
        text: '名称',
        flex: 1,
        dataIndex: 'name',
        width: 200,
        editor: {
            allowBlank: false
        }
    }, {
        text: '节点类型',
        dataIndex: 'type',
        width: 150,
        editor: {
            allowBlank: false,
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
        text: '上传端口',
        dataIndex: 'addr104',
        width: 100,
        editor: {
            allowBlank: false
        }
    }, {
        text: '是否关联',
        dataIndex: 'device',
        width: 80,
        renderer: function (val, column, row) {
            if (val) {
                return "<img src='res/toolbar/true.png'>"
            } else {
                return "<img src='res/toolbar/false.png'>"
            }
        }
    }, {

        xtype: 'checkcolumn',
        text: '是否有效',
        width: 80,
        dataIndex: 'enable',
        stopSelection: false
    }

    ],


    tools: [{
        type: 'refresh',
        tooltip: '当修改了设备、通道、节点、联动后需要保存配置才能生效！',
        // hidden:true,
        handler: function (event, toolEl, panelHeader) {
            ry.serverReload();
        }
    }],

    initComponent: function () {
        this.callParent(arguments);
    }
});
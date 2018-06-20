/**
 * Created by danny on 17-1-4.
 *
 * 用于 Device 的 Node 子表
 */

Ext.define('app.view.admin.panel.NodeForDevice', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.nodefordevice',
    itemId: 'admin_panel_NodeForDevice',

    title: '已经配置的节点',
    icon: 'res/toolbar/node.png',
    // 定义 Model
    store: 'NodeForDevice',
    // 定义 autoload
    autoload: false,
    // 主表id
    master: 'adminDeviceGrid',
    // 定义 外键colums
    fkey: 'device',

    plugins: [{
        ptype: 'grid.editing',
        autoLoad: false,
        hideAdd: true,
        masterGrid: 'adminDeviceGrid',
        fKey: 'device',
    }],

    columns: [
        {
            text: 'ID',
            dataIndex: 'id'
        },

        {
            text: '名称',
            dataIndex: 'name',
            flex: 1,
            editor: {
                allowBlank: false
            }
        },

        {
            text: '端口功能',
            dataIndex: 'devicefun',
            editor: {
                xtype: 'combobox',
                store: ry.DEVICE_FUN
            },
            renderer: function (val, column, row) {
                return ry.trans(val, ry.DEVICE_FUN);
            }
        }
    ],

    viewConfig: {
        plugins: {
            dropGroup: 'groupNode',
            ptype: 'gridDragPlugin',
            enableDrop: true,
            enableDrag: false
        }
    }
})
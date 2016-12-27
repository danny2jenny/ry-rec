/**
 * Created by danny on 16-12-22.
 *
 * Device 配置 Panel 的基类，用于管理Device和Node直接的联系
 *
 * 1、数据库访问
 * 2、拖放的支持
 */


Ext.define('app.view.device.Base', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.device.panel.base',
    title: 'Device 配置基类',
    height: 300,

    tools: [
        {
            type: 'refresh',
            fun: 1,
            tooltip: '刷新当前数据'
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
    },
    // 拖放
    viewConfig: {
        plugins: {
            ddGroup: 'ddg_node',
            ptype: 'gridDragPlugin',
            enableDrop: true,
            enableDrag: false
        }

    },

    listeners: {
        onDragDrop: function (data, targetNode, position) {
            //debugger
        }
    }
});
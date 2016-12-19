/*
 * 管理员的界面框架
 */
Ext.define('scpc.view.Frame', {
    extend: 'Ext.Viewport',
    alias: 'widget.frameMain',
    layout: 'border',
    border: 3,
    items: [

        {
            //横幅
            xtype: 'topPanel',
            region: 'north',
            border: 0,
        },
        {
            // 动态生成的工具条
            xtype: 'dynamic.menu',
            region: 'north'
        },
        {
            //左边的树状菜单
            xtype: 'mainMenu',
            region: 'west',
            width: 300,
            //collapsible :true,
            split: true,
            margins: '5 0 5 5',
        },


        {
            //右边的图表
            xtype: 'tabpanel',
            //icon: '/static/icon/16/chart.png',
            title: '图表',
            itemId: 'chartPanel',
            region: 'east',
            collapsible: true, //panel 可以收方的动画效果
            collapsed: true,
            split: true,
            margins: '5 5 5 0',
            width: 400,
            layout: 'fit',
        },

        {
            region: 'center',
            xtype: 'tabpanel',
            id: 'centerPanel',
            margins: '5 0 5 0',
            layout: 'fit',
            deferredRender: false,
            resizeTabs: true, // turn on tab resizing
            minTabWidth: 115,
            enableTabScroll: true,
            activeTab: 0,
        },

    ],


    initComponent: function () {
        this.callParent(arguments);
    }

});

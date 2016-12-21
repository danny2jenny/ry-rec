/*
 * 管理员的界面框架
 */
Ext.define('ryrec.view.AdminFrame', {
    extend: 'Ext.Viewport',
    alias: 'widget.frameMain',
    layout: 'border',
    border: 3,
    items: [
        {
            region: 'center',
            xtype: 'tabpanel',
            id: 'centerPanel',
            margins: '5 5 0 5',
            layout: 'fit',
            deferredRender: false,
            resizeTabs: true, // turn on tab resizing
            minTabWidth: 115,
            enableTabScroll: true,
            activeTab: 0,
            items: [
                {
                    xtype: 'admin.panel.channelnode'
                },

            ]
        },

    ],


    initComponent: function () {
        this.callParent(arguments);
    }

});

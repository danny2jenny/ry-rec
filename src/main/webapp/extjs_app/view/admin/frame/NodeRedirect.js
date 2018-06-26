Ext.define('app.view.admin.frame.NodeRedirect', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.noderedirect',
    layout: 'border',
    icon: 'res/toolbar/platform.png',
    title: '节点复用',
    items: [
        {
            xtype: 'panel',
            region: 'center',
            layout: 'border',
            split: true,
            items: [
                {
                    xtype: 'admin.panel.allnode',
                    region: 'center'
                },
                {
                    xtype: 'admin.panel.noderedirect',
                    margins: '0 0 5 0',
                    region: 'south',
                    height: 300,
                    split: true
                },
            ]
        },

        {
            xtype: 'admin.panel.icdupload',
            region: 'east',
            width: 300
        }],


    initComponent: function () {
        this.callParent(arguments);
    }
});
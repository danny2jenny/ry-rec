Ext.define('app.view.admin.frame.Iec61850', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.iec61850',
    layout: 'border',
    icon: 'res/toolbar/platform.png',
    title: '平台上传',
    items: [
        {
            xtype: 'admin.panel.iec61850',
            region: 'center'
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
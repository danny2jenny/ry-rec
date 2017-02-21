/**
 * Created by danny on 17-2-16.
 */
Ext.define('app.view.admin.frame.Cooperate', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.cooperate',
    layout: 'border',
    icon: '/icon/toolbar/gis.png',
    title: '联动配置',
    items: [

        {
            xtype: 'admin.panel.ActionRule',
            region: 'center',
            margins: '0 0 0 0'
        },
        {
            xtype:'admin.panel.Actions',
            margins: '0 0 5 0',
            region: 'south',
            height: 500,
        }
    ]
})
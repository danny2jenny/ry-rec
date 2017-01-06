/**
 * Created by danny on 17-1-5.
 */

Ext.define('app.view.admin.frame.GisEditor', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.frame.giseditor',
    layout: 'border',
    icon: '/icon/toolbar/gis.png',
    title: 'GIS 编辑',
    items: [{
        xtype:'gis.view',
        region: 'center'
    },{
        xtype:'admin.panel.formlayerupload',
        region: 'east',
        width: 300
    }]
})

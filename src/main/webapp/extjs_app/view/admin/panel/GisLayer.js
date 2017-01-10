/**
 * Created by danny on 17-1-6.
 */

Ext.define('app.view.admin.panel.GisLayer', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.gislayer',

    itemId: 'admin.panel.gislayer',
    title: 'GIS 图层',
    store: 'GisLayer',


    columns: [
        {
            text: 'ID',
            dataIndex: 'id'
        },
        {
            text: '图层名称',
            dataIndex: 'name',
            flex: 1,
        }],

    plugins: [{
        ptype: 'grid.editing',
        autoLoad: true,
    }]
})

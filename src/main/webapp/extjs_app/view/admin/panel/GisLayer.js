/**
 * Created by danny on 17-1-6.
 * Gis Layer 图层列表
 */

Ext.define('app.view.admin.panel.GisLayer', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.gislayer',
    id: 'admin.panel.gislayer',
    itemId: 'grid_gislayer',
    title: 'GIS 图层',
    icon: 'res/toolbar/layer.png',
    store: 'GisLayer',


    columns: [
        {
            text: 'ID',
            width: 40,
            dataIndex: 'id'
        }, {
            text: '图层名称',
            dataIndex: 'name',
            flex: 1,
            editor: {
                allowBlank: false
            }
        }, {
            text: '分区编号',
            dataIndex: 'zoom',
            width: 80,
            editor: {
                allowBlank: false,
                xtype: 'numberfield',
            }
        }],

    plugins: [{
        ptype: 'grid.editing',
        hideAdd: true,
        autoLoad: true
    }]
});

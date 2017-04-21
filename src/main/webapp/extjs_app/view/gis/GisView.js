/**
 * Created by 12793 on 2017/4/14.
 */


Ext.define('app.view.gis.GisView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.gis.view',
    id: 'gis.view',

    requires: [
        'app.lib.GisViewPlugin'
    ],

    plugins: [{
        ptype: 'gis.view',
        layerStore: 'GisLayer',
        editable: false
    }],

    initComponent: function () {
        this.callParent(arguments);
        Ext.StoreMgr.get('GisLayer').load();
    }
});
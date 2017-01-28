/**
 * Created by danny on 16-12-27.
 */

Ext.define('app.view.gis.GisView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.gis.view',
    itemId: 'gis_view',

    requires: [
        'app.lib.GisViewPlugin'
    ],

    plugins: [{
        ptype: 'gis.view',
        layerStore: 'GisLayer',
        editable: true
    }],

    initComponent: function () {
        this.callParent(arguments);
    }
});
/**
 * Created by danny on 16-12-27.
 */

Ext.define('app.view.gis.GisView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.gis.view',
    title: '系统属性配置',
    icon: '/icon/toolbar/config.png',
    layout: 'fit',

    listeners: {
        resize: function () {
            gis.map.updateSize();
        }
    },

    afterLayout: function () {
        this.callParent();
        gis.map.setTarget(this.body.dom);
    },

    initComponent: function () {
        this.callParent(arguments);
    }

})
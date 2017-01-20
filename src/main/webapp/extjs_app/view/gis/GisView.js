/**
 * Created by danny on 16-12-27.
 */

Ext.define('app.view.gis.GisView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.gis.view',
    layout: 'fit',  //必须是 fit

    listeners: {
        resize: function () {
            gis.map.updateSize();
        }
    },

    afterLayout: function () {
        this.callParent();
        gis.map.setTarget(this.body.dom);
        // 窗口改变大小后，需要重新缩放图层
        gis.map.maxExtent();
    },

    initComponent: function () {
        this.callParent(arguments);

        // 初始化图层
        var layerStore = Ext.StoreMgr.get('GisLayer');
        layerStore.on('refresh', this.onLayerFresh, this);
    },

    // 当Layer Store 刷新时触发
    onLayerFresh: function (store, opt) {

        //首删除所有的图层
        gis.clearLayers();

        //添加相应的图层
        for (var i = 0; i < store.data.getCount(); i++) {
            var item = store.data.getAt(i).data;
            gis.addLayer(item.id, item.name, item.file);
        }

        //把第一个层作为显示层
        gis.layers.getValues()[0].setVisible(true);

        this.on('show', function (from, eOpts) {
            var node = Ext.ComponentQuery.query('#adminNodeGridForDevice')[0];
            var gis = Ext.ComponentQuery.query('#admin.panel.gis')[0];
            node.hide();
            gis.show();
        })
    }

});
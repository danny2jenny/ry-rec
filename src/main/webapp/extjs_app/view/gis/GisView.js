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
    },

    initComponent: function () {
        this.callParent(arguments);


        // 当数据改变成功后的回调
        Ext.direct.Manager.on('event', function (event, provider, eOpts) {
            // 当 Device 删除后，应该刷新 Node
            if (event.action == 'extGis' && event.method == 'saveFeature') {
                // 一个作图已经存储
                gis.draw.drawing = false;

                // 需要对刚刚画的图形的feature进行更新
                // 插入的 gis ID
                gis.draw.lastFeature.setId(event.result.id);
            }

            if (event.action == 'extGis' && event.method == 'getFeaturesByLayer') {
                gis.loadFeatures(gis.layers.device, event.result)
            }

        });
    }

})
/**
 * Created by danny on 17-1-4.
 *
 * Gis 数据的列表
 */

Ext.define('app.view.admin.panel.Gis', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.gis',
    itemId: 'admin_panel_gis',
    id: 'admin.panel.gis',
    icon: 'res/toolbar/gis.png',
    title: 'GIS',
    store: 'Gis',
    columns: [
        {
            text: 'ID',
            dataIndex: 'id'
        },
        {
            text: '类型',
            dataIndex: 'type',
            flex: 1,
            renderer: function (val, column, row) {
                return ry.trans(val, ry.GIS_FEATURE_TYPE);
            }
        }],

    plugins: [{
        ptype: 'grid.editing',
        autoLoad: false,
        hideAdd: true,
        masterGrid: 'adminDeviceGrid',
        fKey: 'device',
        newRec: {}          //新建记录的缺省值
    }],

    // 初始化
    initComponent: function () {
        this.callParent(arguments);

        Ext.direct.Manager.on('event', function (event, provider, eOpts) {

            // 上传图层后，刷新图层列表
            if (event.action == "extVideo" && event.method == "listNvr") {
                var cfg = JSON.stringify(event.result);
                if (videoPlayer) {
                    videoPlayer.initConfig(cfg);
                }
            }
        });
    }
})

/**
 * Created by danny on 17-1-21.
 */


Ext.define('app.view.admin.window.NodeConfig', {
    extend: 'Ext.window.Window',
    alias: 'widget.admin.window.nodeconfig',

    height: 130,
    width: 300,
    resizable: true,
    modal: true,
    layout: {
        type: "fit"
    },
    title: "节点参数配置",
});
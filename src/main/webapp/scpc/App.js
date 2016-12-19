/**
 * Created by danny on 16-9-17.
 *
 * App中只需要引入Controller，
 * View、Store、Model在Controller中进行引入
 */

Ext.Loader.setConfig({
    enabled: true
});

//初始化快速提示
Ext.QuickTips.init();

//应用程序入口
Ext.application({
    name: 'scpc',
    appFolder: '/static/scpc',

    controllers: [
        'Frame',        //主界面框架
        'MainMenu',     //主菜单
        'DataGrid'      //数据显示的表格
    ],


    launch: function () {

        Ext.Loader.setConfig({
            enabled: true
        });

        // 防止插件遮挡窗口
        Ext.useShims = true;

        // 全局屏蔽浏览器右键菜单
        Ext.getBody().on("contextmenu", Ext.emptyFn, null, {
            preventDefault: true
        });

        // 建立主界面
        Ext.create('scpc.view.Frame', {})
        ;
    }
});
/**
 * Created by 12793 on 2017/4/14.
 * 用户的界面
 */

Ext.define('app.view.UserFrame', {
    extend: 'Ext.Viewport',
    alias: 'widget.frameUser',
    layout: 'border',
    border: 3,

    items: [

        // 中间的 TabPanel
        {
            region: 'center',
            xtype: 'tabpanel',
            id: 'centerPanel',
            margins: '0 0 0 0',
            layout: 'fit',
            deferredRender: false,
            resizeTabs: true, // turn on tab resizing
            //minTabWidth: 115,
            enableTabScroll: true,
            activeTab: 0,
            items: [
                {
                    xtype: 'panel',
                    icon: 'res/toolbar/gis.png',
                    title: '地图浏览',
                    layout: 'border',
                    items: [
                        {
                            xtype: 'gis.view',
                            region: 'center'
                        }, {
                            region: 'east',
                            xtype: 'user.alarm',
                            width: 320
                        }
                    ]

                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
    }

});
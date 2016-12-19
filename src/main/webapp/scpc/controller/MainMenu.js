/**
 * Created by danny on 16-9-29.
 */

/**左侧菜单
 *
 */
Ext.define('scpc.controller.MainMenu', {
    extend: 'Ext.app.Controller',
    views: ['MainMenu', 'DynamicMenu'],
    stores: ['MainMenu', 'YearList'],
    models: ['MainMenu'],

    init: function () {
        this.control({
            'mainMenu': {
                itemclick: this.treeItemClick,
                afterrender: this.onAfterrender
            },
            'combobox': {
                change: this.yearSelectChange
            }
        });
    },

    /**
     * 界面建立时只调用一次
     *
     * @param {}
     *            pa
     * @param {}
     *            options
     */
    onAfterrender: function (pa, options) {
        //根据浏览器宽度 动态设置treepanel 宽度
        //var width = document.documentElement.clientWidth * 0.2;
        //alert(width);
        //pa.setWidth(width);
    },

    yearSelectChange: function (scope, newValue, oldValue, eOpts) {
        ry.SelectedYear = newValue;
    },


    /**树性菜单点击事件.
     *
     * @param {} view
     * @param {} record
     * @param {} item
     * @param {} index
     * @param {} eventObj
     */
    treeItemClick: function (view, record, item, index, eventObj) {
        //根据id属性配置获取
        var centerPanel = Ext.getCmp('centerPanel');
        var n = centerPanel.getComponent(record.raw.action);
        if (!n && record.data.leaf == true) { // 判断是否已经打开该面板 (为叶子节点时执行点击事件)
            n = centerPanel.add({
                xtype: record.raw.action,
                itemId: record.raw.action,
                icon: '/static/icon/16/' + record.raw.icon + '.png',
                title: record.raw.name,
                closable: true // 通过html载入目标页  ,显示关闭按钮
            });
        }


        if (n) {
            centerPanel.setActiveTab(n);
            n.store.load({
                scope: n.store,
                callback: storeCallback,
                params: {
                    year: ry.SelectedYear
                }
            });
        }

    }
});

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

// Extjs Direct API
Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

//应用程序入口
Ext.application({
    name: 'app',
    appFolder: '/extjs_app',

    controllers: [
        'AdminFrame'        //主界面框架
    ],

    views: [
        'AdminFrame'
    ],

    requires: [
        'app.lib.GridDragPlugin',
        'app.device.Device_101',
        'app.device.Device_102',
        'app.device.Device_201'
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
        Ext.create('app.view.AdminFrame', {});
    }
});

/**
 * 修复 Extjs 4.2.1 提交按钮的bug
 * https://www.sencha.com/forum/showthread.php?265173
 */
Ext.override(Ext.grid.RowEditor, {
    addFieldsForColumn: function (column, initial) {
        var me = this,
            i,
            length,
            field;

        if (Ext.isArray(column)) {
            for (i = 0, length = column.length; i < length; i++) {
                me.addFieldsForColumn(column[i], initial);
            }
            return;
        }

        if (column.getEditor) {
            // Get a default display field if necessary
            field = column.getEditor(null, {
                xtype: 'displayfield',
                // Override Field's implementation so that the default display fields will not return values. This is done because
                // the display field will pick up column renderers from the grid.
                getModelData: function () {
                    return null;
                }
            });

            me.mon(field, 'change', me.onFieldChange, me);
            if (column.align === 'right') {
                field.fieldStyle = 'text-align:right';
            }

            if (column.xtype === 'actioncolumn') {
                field.fieldCls += ' ' + Ext.baseCSSPrefix + 'form-action-col-field';
            }

            if (me.isVisible() && me.context) {
                if (field.is('displayfield')) {
                    me.renderColumnData(field, me.context.record, column);
                } else {
                    field.suspendEvents();
                    field.setValue(me.context.record.get(column.dataIndex));
                    field.resumeEvents();
                }
            }

            if (column.hidden) {
                me.onColumnHide(column);
            } else if (column.rendered && !initial) {
                // Setting after initial render
                me.onColumnShow(column);
            }
        }
    }
});
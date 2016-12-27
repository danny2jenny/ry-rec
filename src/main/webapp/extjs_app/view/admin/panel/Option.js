/**
 * Created by danny on 16-12-22.
 *
 * 数据库中的全局配置
 */

Ext.define('app.view.admin.panel.Option', {
    extend: 'app.lib.MasterSlaveGride',
    alias: 'widget.admin.panel.option',
    title: '系统属性配置',
    icon: '/icon/toolbar/config.png',
    //hideHeaders: true,

    store: 'Option',

    // 定义 colums
    columns: [
        {
            text: 'ID',
            dataIndex: 'id'
        },

        {
            text: '分类',
            dataIndex: 'cat',
            editor: {
                xtype: 'combobox',
                store: ry.OPT_CAT,
            },
            renderer: function (val, column, row) {
                return ry.trans(val, ry.OPT_CAT);
            }
        },

        {
            text: '值',
            dataIndex: 'value',
            editor: {
                allowBlank: false
            }
        },

        {
            text: '名称',
            width: 40,
            sortable: false,
            dataIndex: 'name',
            menuDisabled: true,
            flex: 1,

            editor: {
                allowBlank: false
            }
        }
    ],


    // 分组
    features: [Ext.create('Ext.grid.feature.Grouping', {
        groupHeaderTpl: ['{groupValue:this.formatValue}: 共 ({rows.length}) 个', {
            formatValue: function (value) {
                return "分类"
            }
        }]
    })]


})

/**
 * Created by danny on 16-12-22.
 *
 * 数据库中的全局配置
 */

Ext.define('app.view.admin.panel.Option', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.admin.panel.option',
    title: '系统属性配置',
    icon: 'res/toolbar/config.png',
    //hideHeaders: true,

    store: 'Option',


    plugins: [{
        ptype: 'grid.editing',
        autoLoad: true,
    }],

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
        },
        {
            text: '设备类型',
            width: 40,
            sortable: false,
            dataIndex: 'type',
            menuDisabled: true,
            flex: 1,

            editor: {
                allowBlank: false
            }
        },
        {
            text: '设备功能',
            width: 40,
            sortable: false,
            dataIndex: 'fun',
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
                return ry.trans(value, ry.OPT_CAT)
            }
        }]
    })]


})

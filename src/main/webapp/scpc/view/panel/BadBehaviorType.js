Ext.define('scpc.view.panel.BadBehaviorType', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.bad.behavior.type',
    title: '施工企业不良行文分类标准',
    icon: 's_equipment',
    // 定义 store
    store: 'BadBehaviorType',

    // 定义 colums
    columns: [
        //设置grid 行号
        {
            xtype: 'rownumberer',
            align: 'center',  //设置列头及单元格的对齐方向。 可取值: 'left', 'center', and 'right'
            minWidth: 30,
            text: '序号'
        },
        {
            text: '不良行为等级',
            align: 'center',
            width: 200,
            sortable: true,
            dataIndex: 'type',
            renderer: function (val) {
                return ry.trans(val, ry.BadBehaviorLevel);
            }
        },
        {
            text: '诚信',
            align: 'center',
            width: 400,
            dataIndex: 'sincerity',
            renderer: function (value, meta, record) {
                //自动换行渲染
                meta.style = 'white-space:normal;word-break:break-all;';
                return value;
            }
        },
        {
            text: '安全质量',
            align: 'center',
            width: 400,
            dataIndex: 'quality',
            renderer: function (value, meta, record) {
                //自动换行渲染
                meta.style = 'white-space:normal;word-break:break-all;';
                return value;
            }
        },
        {
            text: '履约进度',
            align: 'center',
            width: 400,
            dataIndex: 'progress',
            renderer: function (value, meta, record) {
                //自动换行渲染
                meta.style = 'white-space:normal;word-break:break-all;';
                return value;
            }
        },
        {
            text: '服务',
            align: 'center',
            width: 400,
            dataIndex: 'service',
            renderer: function (value, meta, record) {
                //自动换行渲染
                meta.style = 'white-space:normal;word-break:break-all;';
                return value;
            }
        },
        {
            text: '其他',
            align: 'center',
            width: 400,
            dataIndex: 'other',
            renderer: function (value, meta, record) {
                //自动换行渲染
                meta.style = 'white-space:normal;word-break:break-all;';
                return value;
            }
        },
    ]
});

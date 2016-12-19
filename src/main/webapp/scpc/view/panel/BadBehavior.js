Ext.define('scpc.view.panel.BadBehavior', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.bad.behavior',
    title: '不良行为影响修正系数',
    // 定义 store
    store: 'BadBehavior',

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
            text: '施工企业名称',
            align: 'center',
            width: 200,
            sortable: true,
            dataIndex: 'enterprise',
            renderer: function (val) {
                return ry.trans(val, ry.Enterprise);
            }
        },
        {
            text: '不良行为发生年份',
            align: 'center',
            width: 200,
            sortable: true,
            dataIndex: 'year',
        },
        {
            text: "不良行为",
            columns: [
                {
                    text: '严重不良行为次数',
                    align: 'center',
                    width: 200,
                    dataIndex: 'serious',

                },
                {
                    text: '一般不良行为次数',
                    align: 'center',
                    width: 200,
                    dataIndex: 'general',
                }


            ]
        },
        {
            text: '修正系数',
            align: 'center',
            width: 100,
            dataIndex: 'weight'
        },
        {
            text: '记录创建时间',
            align: 'center',
            width: 200,
            dataIndex: 'modified'
        }
    ]


});

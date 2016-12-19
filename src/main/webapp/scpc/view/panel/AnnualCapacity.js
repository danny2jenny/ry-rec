Ext.define('scpc.view.panel.AnnualCapacity', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.annual.capacity',
    title: '施工项目团队工程施工产值',
    icon: '/static/icon/16/4.png',
    // 定义 store
    store: 'AnnualCapacity',

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
            text: '电压等级',
            align: 'center',
            width: 200,
            sortable: true,
            dataIndex: 'voltageRank',
            renderer: function (val) {
                return ry.trans(val, ry.RankVoltage);
            }
        },
        {
            text: '施工项目类型',
            align: 'center',
            width: 200,
            sortable: true,
            dataIndex: 'projectType',
            renderer: function (val) {
                return ry.trans(val, ry.ProjectType);
            }
        },
        {
            text: '合理工期(月)',
            align: 'center',
            width: 100,
            dataIndex: 'reasonablePeriod',
        },
        {
            text: '单个项目部完成<br/>项目数量(个/年)',
            align: 'center',
            width: 100,
            dataIndex: 'oneGroupPerYear',
        },
        {
            text: '年产值(万元/年)',
            align: 'center',
            width: 100,
            dataIndex: 'annualCapacity',
        },
        {
            text: '备注',
            align: 'center',
            width: 400,
            dataIndex: 'memo',
        },
    ]
});

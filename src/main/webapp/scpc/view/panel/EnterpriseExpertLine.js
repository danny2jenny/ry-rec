Ext.define('scpc.view.panel.EnterpriseExpertLine', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.enterprise.expert.line',
    title: '施工企业现场管理人员(线路专业人员)',
    // 定义 store
    store: 'EnterpriseExpertLine',
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
            text: "专业人员",
            columns: [
                {
                    text: '500kv<br/>项目经理',
                    align: 'center',
                    width: 80,
                    dataIndex: 'manager50',
                },
                {
                    text: '220kv及以下<br/>项目经理',
                    align: 'center',
                    width: 80,
                    dataIndex: 'manager22',
                },
                {
                    text: '220kv及以上<br/>项目总工',
                    align: 'center',
                    width: 80,
                    dataIndex: 'engineer22',
                },
                {
                    text: '110kv<br/>项目总工',
                    align: 'center',
                    width: 80,
                    dataIndex: 'engineer11',
                },
                {
                    text: '安全员',
                    align: 'center',
                    width: 80,
                    dataIndex: 'security',
                },
                {
                    text: '质检员',
                    align: 'center',
                    width: 80,
                    dataIndex: 'inspector',
                },
                {
                    text: '技术员',
                    align: 'center',
                    width: 100,
                    dataIndex: 'technician',
                },
                {
                    text: '造价员',
                    align: 'center',
                    width: 100,
                    dataIndex: 'cost',
                },
                {
                    text: '资料员',
                    align: 'center',
                    width: 80,
                    dataIndex: 'documentor',
                },
                {
                    text: '综合管理员',
                    align: 'center',
                    width: 100,
                    dataIndex: 'administrator',
                },
                {
                    text: '材料员',
                    align: 'center',
                    width: 100,
                    dataIndex: 'material',
                },
                {
                    text: '协调员',
                    align: 'center',
                    width: 80,
                    dataIndex: 'coordinator',
                },
                {
                    text: '施工队长',
                    align: 'center',
                    width: 100,
                    dataIndex: 'captain',
                },
                {
                    text: '施工队,技术员<br/>质检员,兼职安全员',
                    align: 'center',
                    width: 150,
                    dataIndex: 'parttime',
                }
                ,
                {
                    text: '合计',
                    align: 'center',
                    width: 100,
                    dataIndex: 'sum'
                }

            ]
        },
        {
            text: '记录创建时间',
            align: 'center',
            width: 200,
            dataIndex: 'modified'
        }

    ]
});

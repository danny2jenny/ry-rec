Ext.define('scpc.view.panel.StaffRequirement', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.staff.requirement',
    title: '企业自身主要人员评估(施工项目部人员构成)',
    icon: '/static/icon/16/2.png',
    // 定义 store
    store: 'StaffRequirement',

    features: [{
        ftype: 'summary'
    }],

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
            text: '岗位',
            align: 'center',
            width: 300,
            sortable: true,
            dataIndex: 'duty',
            renderer: function (val) {
                return ry.trans(val, ry.Duty);
            }
        },
        {
            text: '持证要求',
            align: 'center',
            width: 300,
            dataIndex: 'requirement',
        },
        {
            text: "线路工程(人数)",

            columns: [
                {
                    text: '平地丘陵',
                    align: 'center',
                    width: 100,
                    dataIndex: 'linePlainStaff',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                },
                {
                    text: '山区',
                    align: 'center',
                    width: 100,
                    dataIndex: 'lineMountainStaff',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }

                }
            ]
        },
        {
            text: '变电工程(人数)',
            align: 'center',
            width: 100,
            dataIndex: 'stationStaff',
            summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
            summaryRenderer: function (value, summaryData, dataIndex) {
                return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
            }
        },
        {
            text: '是否允许<br/>其他岗位<br/>人员兼职',
            align: 'center',
            width: 100,
            sortable: true,
            dataIndex: 'partTime',
            renderer: function (val) {
                return ry.trans(val, ry.YesOrNo);
            }
        },
        {
            text: '备注',
            align: 'center',
            width: 400,
            dataIndex: 'memo',
        }
    ]
});

Ext.define('scpc.view.panel.ProjectsPlain', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.projects.plain',
    title: '下一年公司新开工项目情况',
    store: 'ProjectsPlain',

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
            text: '电压等级',
            align: 'center',
            width: 100,
            sortable: true,
            dataIndex: 'voltageRank',
            renderer: function (val) {
                return ry.trans(val, ry.RankVoltage);
            }
        },
        {
            text: '项目年份',
            align: 'center',
            width: 100,
            sortable: true,
            dataIndex: 'year',
        },
        {
            text: '项目个数(个)',
            align: 'center',
            width: 100,
            dataIndex: 'projectNum',
            summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
            summaryRenderer: function (value, summaryData, dataIndex) {
                return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
            }
        },
        {
            text: "标包数(个) ",
            columns: [
                {
                    text: '线路工程数(个)',
                    align: 'center',
                    width: 150,
                    dataIndex: 'lineNum',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                },
                {
                    text: '变电工程数(个)',
                    align: 'center',
                    width: 150,
                    dataIndex: 'stationNum',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                }
            ]
        },
        {
            text: "评估可承担个数(个) ",

            columns: [
                {
                    text: '线路工程数(个)',
                    align: 'center',
                    width: 150,
                    dataIndex: 'capabilityLine',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                },
                {
                    text: '变电工程数(个)',
                    align: 'center',
                    width: 100,
                    dataIndex: 'capabilityStation',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                }
            ]
        },
        {
            text: '线路长度(公里)',
            align: 'center',
            width: 100,
            dataIndex: 'lineLength',
            summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
            summaryRenderer: function (value, summaryData, dataIndex) {
                return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
            }
        },
        {
            text: '变电容量(万千伏安)',
            align: 'center',
            width: 150,
            dataIndex: 'stationVA',
            summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
            summaryRenderer: function (value, summaryData, dataIndex) {
                return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
            }
        },
        {
            text: '总投资(亿元)',
            align: 'center',
            width: 150,
            dataIndex: 'investment',
            summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
            summaryRenderer: function (value, summaryData, dataIndex) {
                return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
            }
        },
        {
            text: '记录创建时间',
            align: 'center',
            width: 200,
            dataIndex: 'modified'
        }
    ],
});

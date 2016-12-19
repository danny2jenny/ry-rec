Ext.define('scpc.view.panel.SurplusCapability', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.surplus.capability',
    title: '施工单位剩余承载能力',
    // 定义 store
    store: 'SurplusCapability',

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
            text: '主管单位',
            align: 'center',
            width: 100,
            sortable: true,
            dataIndex: 'supervisor'
        },
        {
            text: '施工企业名称',
            align: 'center',
            width: 200,
            sortable: true,
            dataIndex: 'name'

        },

        {
            text: "还可以承担线路工程",

            columns: [
                {
                    text: '500kV(个)',
                    align: 'center',
                    width: 100,
                    dataIndex: 'line500',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                },
                {
                    text: '220kV(个)',
                    align: 'center',
                    width: 100,
                    dataIndex: 'line220',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }

                }, {
                    text: '110kV(个)',
                    align: 'center',
                    width: 100,
                    dataIndex: 'line110',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                }]
        },
        {
            text: "还可以承担变电工程",

            columns: [
                {
                    text: '500kV(个)',
                    align: 'center',
                    width: 100,
                    dataIndex: 'station500',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                },
                {
                    text: '220kV(个)',
                    align: 'center',
                    width: 100,
                    dataIndex: 'station220',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                },
                {
                    text: '110kV(个)',
                    align: 'center',
                    width: 100,
                    dataIndex: 'station110',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                }]
        }


    ]

});

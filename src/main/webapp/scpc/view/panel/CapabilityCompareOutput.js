Ext.define('scpc.view.panel.CapabilityCompareOutput', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.capability.compare.output',
    title: '产值对比情况',
    // 定义 store
    store: 'CapabilityCompareOutput',

    features: [{
        ftype: 'summary'
    }],

    // 定义 colums
    columns: [
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
            dataIndex: 'supervisor'
        },

        {
            text: '施工企业名称',
            align: 'center',
            width: 150,
            dataIndex: 'name'
        },

        {
            text: '评估值(万元)',
            align: 'center',
            width: 150,
            dataIndex: 'outputSound',

            summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
            summaryRenderer: function (value, summaryData, dataIndex) {
                return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
            }
        },
        {
            text: '近三年产值平均值(万元)',
            align: 'center',
            width: 150,
            dataIndex: 'outputAverage',

            summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
            summaryRenderer: function (value, summaryData, dataIndex) {
                return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
            }
        },
        {
            text: '差额(万元)',
            align: 'center',
            width: 150,
            dataIndex: 'outputDiff',
            summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
            summaryRenderer: function (value, summaryData, dataIndex) {
                return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
            }
        }
    ],
});

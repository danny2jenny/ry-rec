Ext.define('scpc.view.panel.EnterpriseEquipment', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.enterprise.equipment',
    title: '施工装备信息',
    // 定义 store
    store: 'EnterpriseEquipment',

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
            text: '施工企业名称',
            align: 'center',
            width: 200,
            sortable: true,
            dataIndex: 'enterprise',
        },
        {
            text: "施工装备",

            columns: [
                {
                    text: '大张牵设备(套)',
                    align: 'center',
                    width: 200,
                    dataIndex: 'bigEquipment',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }

                },
                {
                    text: '小张牵设备(套)',
                    align: 'center',
                    width: 200,
                    dataIndex: 'smallEquipment',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }

                }
                ,
                {
                    text: '合计',
                    align: 'center',
                    width: 300,
                    dataIndex: 'sum',

                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('小计: {0} ', value, value !== 1 ? 's' : '');
                    }
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

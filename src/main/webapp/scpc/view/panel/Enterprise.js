/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.view.panel.Enterprise', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.data.enterprise',
    title: '企业基本信息',
    icon: '/static/icon/16/enterprise.png',
    //定义 store
    store: 'Enterprise',

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
            align: 'center',  //设置列头及单元格的对齐方向。 可取值: 'left', 'center', and 'right'。
            width: 100,
            dataIndex: 'supervisor',
            editor: {
                xtype: 'textfield',
                allowBlank: false
            }
        },
        {
            text: '企业全称',
            align: 'center',
            width: 250,
            dataIndex: 'fullName',
            editor: {
                xtype: 'textfield',
                allowBlank: false
            }
        },
        {
            text: '企业简称',
            align: 'center',
            width: 100,
            dataIndex: 'shortName',
            editor: {
                xtype: 'textfield',
                allowBlank: false
            }
        },
        {
            text: '企业性质',
            align: 'center',
            width: 100,
            dataIndex: 'attribute',
            renderer: function (val) {
                return ry.trans(val, ry.EnterpriseAttribute);
            }
        },
        {
            text: '注册资本金<br/>(万元)',
            align: 'center',
            width: 100,
            dataIndex: 'register',
        },
        {
            text: '企业净资产<br/>(万元)',
            align: 'center',
            width: 100,
            dataIndex: 'assets',
        },
        {
            text: '在职<br/>职工数',
            align: 'center',
            width: 100,
            dataIndex: 'employee',
        },
        {
            text: "资质 ",
            columns: [
                {
                    text: '总承包<br/>(等级)',
                    align: 'center',
                    width: 100,
                    dataIndex: 'overallRank',
                    renderer: function (val) {
                        return ry.trans(val, ry.ContractLevel);
                    }
                }, {
                    text: '专业承包<br/>(等级)',
                    align: 'center',
                    width: 100,
                    dataIndex: 'professionRank',
                    renderer: function (val) {
                        return ry.trans(val, ry.ContractLevel);
                    }
                }]
        },
        {
            text: "近三年工程数量 ",

            columns: [
                {
                    text: '去年<br/>工程<br/>数量',
                    align: 'center',
                    width: 100,
                    dataIndex: 'project_1',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                }, {
                    text: '前年<br/>工程<br/>数量',
                    align: 'center',
                    width: 100,
                    dataIndex: 'project_2',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                }, {
                    text: '上前年<br/>工程<br/>数量',
                    align: 'center',
                    width: 100,
                    dataIndex: 'project_3',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                }, {
                    text: '合计',
                    align: 'center',
                    width: 100,
                    dataIndex: 'project',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                }

            ]
        },
        {
            text: "近三年产值 ",
            columns: [
                {
                    text: '去年<br/>产值 ',
                    align: 'center',
                    width: 200,
                    dataIndex: 'output_1',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                }, {
                    text: '前年<br/>产值 ',
                    align: 'center',
                    width: 200,
                    dataIndex: 'output_2',
                    //flex : 2,
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }
                }, {
                    text: '上前年<br/>产值 ',
                    align: 'center',
                    width: 200,
                    dataIndex: 'output_3',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
                    }

                }
                , {
                    text: '合计 ',
                    align: 'center',
                    width: 200,
                    dataIndex: 'output',
                    summaryType: 'sum', //引入grid 特征后 - 列求和: 详细见Ext.grid.feature.Summary的api
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        return Ext.String.format('合计: {0} ', value, value !== 1 ? 's' : '');
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
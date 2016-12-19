/**
 * Created by danny on 16-10-14.
 */


Ext.define('scpc.view.panel.EnterpriseStaff', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.data.enterprise.staff',
    title: '企业人员信息',
    icon: '/static/icon/16/person.png',
    // 定义 store
    store: 'EnterpriseStaff',

    // 定义 colums
    columns: [
        //设置grid 行号
        {
            xtype: 'rownumberer',
            align: 'center',  //设置列头及单元格的对齐方向。 可取值: 'left', 'center', and 'right'
            minWidth: 30,
            text: '序号'
        }
        , {
            text: '施工企业名称',
            align: 'center',
            width: 200,
            sortable: true,
            dataIndex: 'enterprise',
        },
        {
            text: "国家注册建造师 ",
            columns: [
                {
                    text: '一级',
                    align: 'center',
                    width: 100,
                    dataIndex: 'constructorFirst',

                }, {
                    text: '二级',
                    align: 'center',
                    width: 100,
                    dataIndex: 'constructorSecond',
                },
                {
                    text: '合计',
                    align: 'center',
                    width: 100,
                    dataIndex: 'constructorAll'
                }
            ]
        },

        {
            text: "中級以上职称人员 ",
            columns: [
                {
                    text: '中级',
                    align: 'center',
                    width: 100,
                    dataIndex: 'titleMiddle',

                }, {
                    text: '高级',
                    align: 'center',
                    width: 100,
                    dataIndex: 'titleHigh',

                },
                {
                    text: '合计',
                    align: 'center',
                    width: 60,
                    dataIndex: 'titleAll'
                }
            ]
        },
        {
            text: "中级工以上技术人员 ",
            columns: [
                {
                    text: '中级',
                    align: 'center',
                    width: 100,
                    dataIndex: 'skillMiddle',
                }, {
                    text: '高级',
                    align: 'center',
                    width: 100,
                    dataIndex: 'skillHigh',
                },
                {
                    text: '技师',
                    align: 'center',
                    width: 100,
                    dataIndex: 'tech',
                },
                {
                    text: '高级技师',
                    align: 'center',
                    width: 100,
                    dataIndex: 'techHigh',
                },
                {
                    text: '合计',
                    align: 'center',
                    width: 100,
                    dataIndex: 'techAll'
                }
            ]
        }, {
            text: '记录创建时间',
            align: 'center',
            width: 200,
            dataIndex: 'modified'
        }
    ]
});

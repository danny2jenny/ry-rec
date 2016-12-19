/**企资质标准分类
 *
 */
Ext.define('scpc.view.panel.CertificationEnterprise', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.certification.enterprise',
    title: '企业资质对施工承载能力评估',
    icon: '/static/icon/16/1.png',
    store: 'CertificationEnterprise',

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
            text: '企业资质',
            align: 'center',
            width: 100,
            sortable: true,
            dataIndex: 'classify',
            renderer: function (val) {
                return ry.trans(val, ry.ContractClass);
            }
        },
        {
            text: '等级',
            align: 'center',
            width: 100,
            sortable: true,
            dataIndex: 'level',
            renderer: function (val) {
                return ry.trans(val, ry.ContractLevel);
            }
        },
        {
            text: '资质要求',
            align: 'center',
            width: 200,
            dataIndex: 'assets',
            renderer: function (value, meta, record) {
                //自动换行渲染
                meta.style = 'white-space:normal;word-break:break-all;';
                return value;
            }
        },
        {
            text: "企业人员",
            columns: [
                {
                    text: '执业资格',
                    align: 'center',
                    width: 300,
                    dataIndex: 'qualification',
                    renderer: function (value, meta, record) {
                        //自动换行渲染
                        meta.style = 'white-space:normal;word-break:break-all;';
                        return value;
                    }
                },
                {
                    text: '技术负责人',
                    align: 'center',
                    width: 300,
                    dataIndex: 'techManager',
                    editor: {
                        xtype: 'textarea',
                        allowBlank: false
                    },
                    renderer: function (value, meta, record) {
                        //自动换行渲染
                        meta.style = 'white-space:normal;word-break:break-all;';
                        return value;
                    }

                },
                {
                    text: '技术人员',
                    align: 'center',
                    width: 300,
                    dataIndex: 'technician',
                    renderer: function (value, meta, record) {
                        //自动换行渲染
                        meta.style = 'white-space:normal;word-break:break-all;';
                        return value;
                    }
                },
                {
                    text: '技能人员',
                    align: 'center',
                    width: 300,
                    dataIndex: 'skillMem',
                    renderer: function (value, meta, record) {
                        //自动换行渲染
                        meta.style = 'white-space:normal;word-break:break-all;';
                        return value;
                    }
                }
            ]
        },
        {
            text: '承包范围',
            align: 'center',
            width: 400,
            dataIndex: 'contractRange',
            renderer: function (value, meta, record) {
                //自动换行渲染
                meta.style = 'white-space:normal;word-break:break-all;';
                return value;
            }
        },
        {
            text: '记录创建时间',
            align: 'center',
            width: 200,
            dataIndex: 'modified'
        }
    ]
});

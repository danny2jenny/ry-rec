Ext.define('scpc.view.panel.EquipmentCapablity', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.equipment.capablity',
    title: '机具设备对工程承载力的评估',
    icon: '/static/icon/16/3.png',
    // 定义 store
    store: 'EquipmentCapablity',

    // 定义 colums
    columns: [//设置grid 行号
        {
            xtype: 'rownumberer',
            align: 'center',  //设置列头及单元格的对齐方向。 可取值: 'left', 'center', and 'right'
            minWidth: 30,
            text: '序号'
        }
        ,
        {
            text: '张牵设备',
            align: 'center',
            width: 200,
            dataIndex: 'name',
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
            text: '单回/双回',
            align: 'center',
            width: 100,
            sortable: true,
            dataIndex: 'lineType',
            renderer: function (val) {
                return ry.trans(val, ry.LineType);
            }
        },
        {
            text: "导线展放效率(km/月.套)",
            columns: [
                {
                    text: '平地丘陵',
                    align: 'center',
                    width: 100,
                    dataIndex: 'linePlainCapacity',
                },
                {
                    text: '山区',
                    align: 'center',
                    width: 100,
                    dataIndex: 'lineMountainCapacity',
                }
            ]
        },


        {
            text: '说明',
            align: 'center',
            width: 600,
            dataIndex: 'memo',
            editor: {
                xtype: 'textarea',
                allowBlank: true
            },
            renderer: function (value, meta, record) {
                //自动换行渲染
                meta.style = 'white-space:normal;word-break:break-all;';
                return value;
            }

        },
    ]
});

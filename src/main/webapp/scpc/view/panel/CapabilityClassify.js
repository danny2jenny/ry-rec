Ext.define('scpc.view.panel.CapabilityClassify', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.capability.classify',
    title: '施工企业评估分类',
    // 定义 store
    store: 'CapabilityClassify',

    // 定义 colums
    columns: [//设置grid 行号
        {
            xtype: 'rownumberer',
            align: 'center',  //设置列头及单元格的对齐方向。 可取值: 'left', 'center', and 'right'
            minWidth: 30,
            text: '序号'
        },
        {
            text: '类别',
            align: 'center',
            width: 100,
            sortable: true,
            dataIndex: 'name',
        },
        {
            text: '标准',
            align: 'center',
            width: 400,
            dataIndex: 'standard',
            renderer: function (value, meta, record) {
                //自动换行渲染
                meta.style = 'white-space:normal;word-break:break-all;';
                return value;
            }
        },
        {
            text: '可承载项目<br/>最少个数(个)',
            align: 'center',
            width: 100,
            dataIndex: 'projectsMin',
        },
        {
            text: '可承载项目<br/>最多个数(个)',
            align: 'center',
            width: 100,
            dataIndex: 'projectsMax',
        },

        {
            text: '可承载工程<br/>最小产值(亿元)',
            align: 'center',
            width: 100,
            dataIndex: 'outputMin',
        },

        {
            text: '可承载工程<br/>最大产值(亿元)',
            align: 'center',
            width: 100,
            dataIndex: 'outputMax',
        },

        {
            text: '施工企业',
            align: 'center',
            width: 600,
            dataIndex: 'enterprises',
        },
    ]
});

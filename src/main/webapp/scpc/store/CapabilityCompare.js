/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.CapabilityCompare', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.CapabilityCompare',
    proxy: {
        type: 'ajax',
        url: 'capability.compare',
        reader: {
            type: 'json'
        }
    },

    // 图表配置参数数组
    chartCfg: [
        {
            type: 'column',
            region: 'center',
            store: 'CapabilityCompare',
            xName: '近三年承担项目分布',
            yName: '数量',
            unit: '个',
            column: 'projectSum',
            columnName: 'name',
            range: [[0, 10], [10, 20], [20, 30], [30, 40], [40, 50], [50, 999999999]]
        },

        {
            type: 'column',
            region: 'center',
            store: 'CapabilityCompare',
            xName: '近三年产值分布',
            yName: '数量',
            unit: '亿',
            column: 'projectSum',
            columnName: 'name',
            range: [[0, 1], [1, 3], [3, 5], [5, 10], [10, 20], [20, 999999999]]
        }
    ]

})
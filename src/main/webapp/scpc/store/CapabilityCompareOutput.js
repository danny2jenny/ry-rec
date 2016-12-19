/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.CapabilityCompareOutput', {
    extend: 'scpc.store.CapabilityCompare',

    // 图表配置参数数组
    chartCfg: [
        {
            type: 'column',
            region: 'center',
            store: 'CapabilityCompareOutput',
            xName: '近三年产值分布',
            yName: '数量',
            unit: '亿',
            column: 'projectSum',
            columnName: 'name',
            range: [[0, 1], [1, 3], [3, 5], [5, 10], [10, 20], [20, 999999999]]
        },

        {
            type: 'column',
            region: 'center',
            store: 'CapabilityCompareOutput',
            xName: '评估产值分布',
            yName: '数量',
            unit: '亿',
            column: 'outputAll',
            columnName: 'name',
            range: [[0, 0.5], [0.5, 1], [1, 2], [2, 3], [3, 4], [4, 999999999]]
        },

        {
            type: 'column',
            region: 'center',
            store: 'CapabilityCompareOutput',
            xName: '产值不足',
            yName: '数量',
            unit: '万',
            column: 'outputDiff',
            columnName: 'name',
            range: [[0, 1000], [1000, 5000], [5000, 10000], [10000, 999999999]]
        },

        {
            type: 'column',
            color: 'red',
            region: 'center',
            store: 'CapabilityCompareOutput',
            xName: '产值超量',
            yName: '数量',
            unit: '万',
            column: 'outputDiff',
            columnName: 'name',
            range: [[-1000, 0], [-5000, -1000], [-10000, -5000], [-999999999, -10000]]
        }
    ]

})
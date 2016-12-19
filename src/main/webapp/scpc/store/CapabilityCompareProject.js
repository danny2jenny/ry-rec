/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.CapabilityCompareProject', {
    extend: 'scpc.store.CapabilityCompare',

    // 图表配置参数数组
    chartCfg: [
        {
            type: 'column',
            region: 'center',
            store: 'CapabilityCompareProject',
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
            store: 'CapabilityCompareProject',
            xName: '评估工程量分布',
            yName: '数量',
            unit: '个',
            column: 'projectAll',
            columnName: 'name',
            range: [[0, 3], [3, 5], [5, 7], [7, 9], [9, 999999999]]
        },

        {
            type: 'column',
            region: 'center',
            store: 'CapabilityCompareProject',
            xName: '承担工程不足',
            yName: '数量',
            unit: '个',
            column: 'projectDiff',
            columnName: 'name',
            range: [[0, 2], [2, 4], [4, 6], [6, 999999999]]
        },

        {
            type: 'column',
            color: 'red',
            region: 'center',
            store: 'CapabilityCompareProject',
            xName: '承担工程超载',
            yName: '数量',
            unit: '个',
            column: 'projectDiff',
            columnName: 'name',
            range: [[-2, 0], [-4, -2], [-6, -4], [-999999999, -6]]
        }
    ]

})
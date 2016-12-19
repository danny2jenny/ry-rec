/**
 * Created by danny on 16-9-30.
 */

Ext.define('scpc.store.CorrectedCapability', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.BaseCapability',
    proxy: {
        type: 'ajax',
        url: 'corrected.capability',
        reader: {
            type: 'json'
        }
    },

    // 图表配置参数数组
    chartCfg: [
        {
            type: 'column',
            region: 'center',
            store: 'CorrectedCapability',
            xName: '项目能力分布',
            yName: '数量',
            unit: '个',
            column: 'projectSum',
            columnName: 'name',
            range: [[0, 3], [3, 5], [5, 7], [7, 9], [9, 999999999]]
        },

        {
            type: 'column',
            region: 'center',
            store: 'CorrectedCapability',
            xName: '产值能力分布',
            yName: '数量',
            unit: '亿',
            column: 'projectSum',
            columnName: 'name',
            range: [[0.5, 1], [1, 2], [2, 3], [4, 4], [4, 999999999]]
        }
    ]
})
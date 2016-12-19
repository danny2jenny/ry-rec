/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.Enterprise', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.Enterprise',
    proxy: {
        type: 'ajax',
        url: 'enterprise',
        reader: {
            type: 'json'
        }

    },

    // 图表配置参数数组
    chartCfg: [
        {
            type: 'column',
            region: 'center',
            store: 'Enterprise',
            xName: '企业净资产范围',
            yName: '数量',
            unit: '万',
            column: 'assets',
            columnName: 'shortName',
            range: [[0, 3000], [3000, 5000], [5000, 10000], [10000, 999999999]]
        }
    ]
})
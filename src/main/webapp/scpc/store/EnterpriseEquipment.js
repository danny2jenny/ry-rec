/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.EnterpriseEquipment', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.EnterpriseEquipment',
    proxy: {
        type: 'ajax',
        url: 'enterprise.equipment',
        reader: {
            type: 'json'
        }
    },

    // 图表配置参数数组
    chartCfg: [
        {
            type: 'column',
            region: 'center',
            store: 'EnterpriseEquipment',
            xName: '企业设备数量范围',
            yName: '数量',
            unit: '台',
            column: 'sum',
            columnName: 'enterprise',
            range: [[0, 1], [1, 5], [5, 10], [10, 999999999]]
        }
    ]
})
/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.EnterpriseStaff', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.EnterpriseStaff',
    proxy: {
        type: 'ajax',
        url: 'enterprise.staff',
        reader: {
            type: 'json'
        }
    },

    chartCfg: [
        {
            type: 'column',
            region: 'center',
            store: 'EnterpriseStaff',
            xName: '企业建造师分布',
            yName: '数量',
            unit: '人',
            column: 'constructorAll',
            columnName: 'enterprise',
            range: [[0, 10], [10, 20], [20, 30], [30, 40], [40, 50], [50, 999999999]]
        },
        {
            type: 'column',
            region: 'center',
            store: 'EnterpriseStaff',
            xName: '中级以上技术人员分布',
            yName: '数量',
            unit: '人',
            column: 'techAll',
            columnName: 'enterprise',
            range: [[0, 30], [30, 40], [40, 50], [50, 75], [75, 150], [150, 999999999]]
        }
    ]
})
Ext.define('scpc.model.EnterpriseExpert', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {name: 'enterprise', type: 'int', sortable: true},
        {name: 'expertType', type: 'int', sortable: true},

        {name: 'manager50', type: 'int', sortable: true, defaultValue: 0},
        {name: 'manager22', type: 'int', sortable: true, defaultValue: 0},
        {name: 'engineer22', type: 'int', sortable: true, defaultValue: 0},
        {name: 'engineer11', type: 'int', sortable: true, defaultValue: 0},
        {name: 'security', type: 'int', sortable: true, defaultValue: 0},
        {name: 'inspector', type: 'int', sortable: true, defaultValue: 0},
        {name: 'technician', type: 'int', sortable: true, defaultValue: 0},
        {name: 'cost', type: 'int', sortable: true, defaultValue: 0},
        {name: 'documentor', type: 'int', sortable: true, defaultValue: 0},
        {name: 'administrator', type: 'int', sortable: true, defaultValue: 0},
        {name: 'material', type: 'int', sortable: true, defaultValue: 0},
        {name: 'coordinator', type: 'int', sortable: true, defaultValue: 0},
        {name: 'captain', type: 'int', sortable: true, defaultValue: 0},
        {name: 'parttime', type: 'int', sortable: true, defaultValue: 0},
        {
            name: 'sum', type: 'int', sortable: true,
            convert: function (value, record) {
                var t = record.get('manager50') +
                    record.get('manager22') +
                    record.get('engineer22') +
                    record.get('engineer11') +
                    record.get('security') +
                    record.get('inspector') +
                    record.get('technician') +
                    record.get('cost') +
                    record.get('documentor') +
                    record.get('administrator') +
                    record.get('material') +
                    record.get('coordinator') +
                    record.get('captain') +
                    record.get('parttime');
                return t
            }
        },
        {name: 'modified', type: 'string', sortable: true},
    ],

});

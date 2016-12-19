Ext.define('scpc.model.ProjectsProgress', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {name: 'enterprise', type: 'int', sortable: true},
        {name: 'year', type: 'int', sortable: true},
        {name: 'process', type: 'int', sortable: true},

        {name: 'line110', type: 'int', sortable: true},
        {name: 'line220', type: 'int', sortable: true},
        {name: 'line500', type: 'int', sortable: true},
        {name: 'station110', type: 'int', sortable: true},
        {name: 'station220', type: 'int', sortable: true},
        {name: 'station500', type: 'int', sortable: true},

        {name: 'modified', type: 'string', sortable: true},
    ]
});

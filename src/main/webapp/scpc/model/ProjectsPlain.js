Ext.define('scpc.model.ProjectsPlain', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {name: 'voltageRank', type: 'int', sortable: true},
        {name: 'year', type: 'int', sortable: true},
        {name: 'projectNum', type: 'int', sortable: true},

        {name: 'lineNum', type: 'int', sortable: true},
        {name: 'stationNum', type: 'int', sortable: true},
        {name: 'capabilityLine', type: 'int', sortable: true},
        {name: 'capabilityStation', type: 'int', sortable: true},
        {name: 'lineLength', type: 'int', sortable: true},
        {name: 'stationVA', type: 'int', sortable: true},
        {name: 'investment', type: 'float', sortable: true},
        {name: 'modified', type: 'string', sortable: true},
    ]
});

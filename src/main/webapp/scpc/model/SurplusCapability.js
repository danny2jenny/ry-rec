Ext.define('scpc.model.SurplusCapability', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'supervisor', type: 'string', sortable: true},
        {name: 'name', type: 'string', sortable: true},

        {name: 'line500', type: 'int', sortable: true},
        {name: 'line220', type: 'int', sortable: true},
        {name: 'line110', type: 'int', sortable: true},

        {name: 'station500', type: 'int', sortable: true},
        {name: 'station220', type: 'int', sortable: true},
        {name: 'station110', type: 'int', sortable: true}
    ]
});

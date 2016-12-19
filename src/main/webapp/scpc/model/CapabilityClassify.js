Ext.define('scpc.model.CapabilityClassify', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {name: 'name', type: 'string', sortable: true},
        {name: 'standard', type: 'string', sortable: true},
        {name: 'projectsMin', type: 'string', sortable: true},
        {name: 'projectsMax', type: 'string', sortable: true},
        {name: 'outputMin', type: 'string', sortable: true},
        {name: 'outputMax', type: 'string', sortable: true},
        {name: 'enterprises', type: 'string', sortable: true},
    ],
});

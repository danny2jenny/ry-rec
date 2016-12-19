Ext.define('scpc.model.BadBehaviorType', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {name: 'type', type: 'int', sortable: true},
        {name: 'sincerity', type: 'string', sortable: true},
        {name: 'quality', type: 'string', sortable: true},
        {name: 'progress', type: 'string', sortable: true},
        {name: 'service', type: 'string', sortable: true},
        {name: 'other', type: 'string', sortable: true},
    ],
});

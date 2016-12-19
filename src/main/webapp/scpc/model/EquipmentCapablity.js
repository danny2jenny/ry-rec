Ext.define('scpc.model.EquipmentCapablity', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {name: 'name', type: 'string', sortable: true},
        {name: 'voltageRank', type: 'int', sortable: true},
        {name: 'lineType', type: 'int', sortable: true},
        {name: 'linePlainCapacity', type: 'int', sortable: true, defaultValue: 1},
        {name: 'lineMountainCapacity', type: 'int', sortable: true, defaultValue: 1},
        {name: 'memo', type: 'String', sortable: true}
    ]
});

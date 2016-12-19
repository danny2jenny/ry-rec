Ext.define('scpc.model.StaffRequirement', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {name: 'duty', type: 'int', sortable: true},
        {name: 'requirement', type: 'strint', sortable: true},
        {name: 'linePlainStaff', type: 'int', sortable: true, defaultValue: 1},
        {name: 'lineMountainStaff', type: 'int', sortable: true, defaultValue: 1},
        {name: 'stationStaff', type: 'int', sortable: true, defaultValue: 1},
        {name: 'partTime', type: 'boolean', sortable: true},
        {name: 'memo', type: 'string', sortable: true},
    ],
});

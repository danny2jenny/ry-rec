/**
 * Created by danny on 16-10-13.
 *
 *
 */

/*

 */

Ext.define('scpc.model.Enterprise', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {name: 'supervisor', type: 'string', sortable: true},
        {name: 'fullName', type: 'string', sortable: true},
        {name: 'shortName', type: 'string', sortable: true},
        {name: 'attribute', type: 'int', sortable: true},
        {name: 'register', type: 'int', sortable: true},
        {name: 'assets', type: 'int', sortable: true},
        {name: 'employee', type: 'int', sortable: true},
        {name: 'overallRank', type: 'int', sortable: true},
        {name: 'professionRank', type: 'int', sortable: true},
        {name: 'output_1', type: 'int', sortable: true},
        {name: 'output_2', type: 'int', sortable: true},
        {name: 'output_3', type: 'int', sortable: true},
        {
            name: 'output', type: 'int', sortable: true,
            convert: function (value, record) {
                return record.get('output_1') + record.get('output_2') + record.get('output_3');
            }
        },
        {name: 'project_1', type: 'int', sortable: true},
        {name: 'project_2', type: 'int', sortable: true},
        {name: 'project_3', type: 'int', sortable: true},
        {
            name: 'project', type: 'int', sortable: true,
            convert: function (value, record) {
                return record.get('project_1') + record.get('project_2') + record.get('project_3');
            }
        },
        {name: 'modified', type: 'string', sortable: true},
    ],
});

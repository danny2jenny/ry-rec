/**
 * Created by danny on 16-10-6.
 */

Ext.define('scpc.model.CertificationEnterprise', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {name: 'classify', type: 'int', sortable: true},
        {name: 'level', type: 'int', sortable: true},
        {name: 'assets', type: 'string', sortable: true},
        {name: 'qualification', type: 'string', sortable: true},
        {name: 'techManager', type: 'string', sortable: true},
        {name: 'technician', type: 'string', sortable: true},
        {name: 'skillMem', type: 'string', sortable: true},
        {name: 'contractRange', type: 'string', sortable: true},
        {name: 'modified', type: 'string', sortable: true}
    ]
});

Ext.define('scpc.model.EnterpriseEquipment', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {
            name: 'enterprise', type: 'int', sortable: true,
            convert: function (value, record) {
                return ry.trans(value, ry.Enterprise);
            }
        },
        {name: 'bigEquipment', type: 'int', sortable: true},
        {name: 'smallEquipment', type: 'int', sortable: true},
        {
            name: 'sum', type: 'int', sortable: true,
            convert: function (value, record) {
                var t = record.get('bigEquipment') + record.get('smallEquipment');
                return t
            }
        },
        {name: 'modified', type: 'string', sortable: true},
    ],


})
;

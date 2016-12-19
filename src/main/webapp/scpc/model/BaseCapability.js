/**
 * Created by danny on 16-10-6.
 */

Ext.define('scpc.model.BaseCapability', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'enterprise', type: 'int', sortable: true},
        {name: 'name', type: 'string', sortable: true,},
        {name: 'line500', type: 'int', sortable: true},
        {name: 'line220', type: 'int', sortable: true},
        {name: 'line110', type: 'int', sortable: true},
        {name: 'lineOutput', type: 'int', sortable: true},
        {name: 'station500', type: 'int', sortable: true},
        {name: 'station220', type: 'int', sortable: true},
        {name: 'station110', type: 'int', sortable: true},
        {name: 'stationOutput', type: 'int', sortable: true},
        {name: 'allOutput', type: 'int', sortable: true},

        {
            name: 'projectSum', type: 'int', sortable: true,
            convert: function (value, record) {
                var t = record.get('line500') + record.get('line220') + record.get('line110');
                return t
            }
        },

        {
            name: 'outputSum', type: 'int', sortable: true,
            convert: function (value, record) {
                var t = record.get('allOutput') / 10000;
                return t
            }
        },
    ]
});

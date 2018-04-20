/**
 * Created by danny on 16-12-20.
 */

Ext.define('app.model.Node', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'cid'},
        {name: 'adr'},
        {name: 'no'},
        {name: 'name'},
        {name: 'type', type: 'int'},
        {name: 'opt'},
        {name: 'device'},
        {name: 'devicefun'},
        {
            name: 'addr104',
            defaultValue: 0
        },
        {
            name: 'enable',
            type: 'boolean',
            convert: function (v) {
                if (typeof v === 'boolean') {
                    return v ? 1 : 0;
                } else {
                    return parseInt(v, 10);
                }
            },
            defaultValue: 1
        }
    ],

    validations: [{
        field: 'name',
        type: 'length',
        min: 2
    }]

});
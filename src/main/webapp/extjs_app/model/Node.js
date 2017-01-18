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
        {name: 'other'},
        {name: 'device'},
        {name: 'devicefun'},
    ],

    validations: [{
        field: 'name',
        type: 'length',
        min: 2
    }]

});
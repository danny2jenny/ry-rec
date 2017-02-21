/**
 * Created by danny on 17-2-17.
 */


Ext.define('app.model.Actions', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'rule'},
        {name: 'target'},
        {name: 'act'},
        {name: 'parm'}
    ],

    validations: [{
        field: 'act',
        type: 'length',
        min: 1
    }]
});
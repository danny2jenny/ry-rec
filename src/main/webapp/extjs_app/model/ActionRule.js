/**
 * Created by danny on 17-2-17.
 */


Ext.define('app.model.ActionRule', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'device'},
        {name: 'sig'},
    ],

    validations: [{
        field: 'sig',
        type: 'length',
        min: 1
    }]
});
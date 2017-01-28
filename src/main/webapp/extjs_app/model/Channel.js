/**
 * Created by danny on 16-12-19.
 */

Ext.define('app.model.Channel', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'name'},
        {name: 'ip'},
        {name: 'port'},
        {name: 'login'},
        {name: 'pass'},
        {name: 'type', type: 'int'},
        {name: 'opt'},
    ],

    validations: [{
        type: 'format',
        field: 'ip',
        matcher: /^((25[0-5])|(2[0-4]\d)|(1\d\d)|([1-9]\d)|\d)(\.((25[0-5])|(2[0-4]\d)|(1\d\d)|([1-9]\d)|\d)){3}$/
    }]
});

/**
 * Created by danny on 16-12-22.
 */

Ext.define('ryrec.model.Option', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'cat', type: 'int'},
        {name: 'name'},
        {name: 'value'}
    ]

});

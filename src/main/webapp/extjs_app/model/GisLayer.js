/**
 * Created by danny on 17-1-6.
 */

Ext.define('app.model.GisLayer', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'name'},
        {name: 'file'},
        {name: 'zoom'}
    ]

});

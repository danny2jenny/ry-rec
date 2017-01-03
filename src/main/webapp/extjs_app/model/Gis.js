/**
 * Created by danny on 17-1-3.
 *
 * GIS 表
 */


Ext.define('app.model.Gis', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'device', type: 'int'},
        {name: 'type'},
        {name: 'layer'},
        {name: 'data'}
    ]

});

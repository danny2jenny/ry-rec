/**
 * Created by danny on 17-5-10.
 */

Ext.define('app.model.PanoramaDevice', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'device', type: 'int'},
        {name: 'scene'},
        {name: 'pitch'},
        {name: 'yaw'}
    ]

});

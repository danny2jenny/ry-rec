/**
 * Created by danny on 16-12-21.
 *
 * 设备列表
 */

Ext.define('app.model.Device', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {
            name: 'no',
            defaultValue: 0
        },
        {name: 'name'},
        {name: 'type'},
        {name: 'icon', defaultValue: 0},
        {name: 'opt'}
    ],

    validations: [{
        field: 'name',
        type: 'length',
        min: 2
    }]

});

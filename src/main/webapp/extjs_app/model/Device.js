/**
 * Created by danny on 16-12-21.
 *
 * 设备列表
 */

Ext.define('app.model.Device', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'no'},
        {name: 'name'},
        {name: 'type'},
        {name: 'icon'},
        {name: 'lnodetype'},
        {name: 'lnodenum'},
    ],

    validations: [{
        field: 'name',
        type: 'length',
        min: 2
    }]

});

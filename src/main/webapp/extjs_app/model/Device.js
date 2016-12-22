/**
 * Created by danny on 16-12-21.
 *
 * 设备列表
 */

Ext.define('ryrec.model.Device', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'no'},
        {name: 'name'},
        {name: 'type'},
        {name: 'lnodetype'},
        {name: 'lnodenum'},
    ]

});

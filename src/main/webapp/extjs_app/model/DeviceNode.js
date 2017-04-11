/**
 * Created by danny on 17-4-11.
 */


Ext.define('app.model.DeviceNode', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'dno'},
        {name: 'dname'},
        {name: 'dtype'},
        {name: 'nid'},
        {name: 'cid'},
        {name: 'nadd'},
        {name: 'nno'},
        {name: 'ntype'},
        {name: 'conf'},
        {name: 'nfun'}
    ]
});

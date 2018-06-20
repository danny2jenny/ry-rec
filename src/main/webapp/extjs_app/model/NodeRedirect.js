/**
 * 节点重新定向到设备
 */

Ext.define('app.model.NodeRedirect', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'node'},
        {name: 'device'},
        {name: 'devicefun'}
    ]
});
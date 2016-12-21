/**
 * Created by danny on 16-12-20.
 */

Ext.define('ryrec.model.admin.Node', {
    extend: 'Ext.data.Model',

    /**fields  type
     */
    fields: [
        {name: 'id', sortable: false},
        {name: 'cid', sortable: false},
        {name: 'adr', sortable: false},
        {name: 'no', sortable: false},
        {name: 'name', sortable: false},
        {name: 'type', sortable: false},
        {name: 'other', sortable: false},
        {name: 'device', sortable: false},
        {name: 'devicefun', sortable: false},
    ],
});
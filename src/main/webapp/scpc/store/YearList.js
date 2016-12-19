/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.YearList', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    //model: 'scpc.model.Enterprise',
    fields: ['name', 'value'],
    proxy: {
        type: 'ajax',
        url: 'year.list',
        reader: {
            type: 'json'
        }
    }
})
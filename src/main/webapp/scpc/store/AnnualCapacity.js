/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.AnnualCapacity', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.AnnualCapacity',
    proxy: {
        type: 'ajax',
        url: 'annual.capacity',
        reader: {
            type: 'json'
        }
    }
})
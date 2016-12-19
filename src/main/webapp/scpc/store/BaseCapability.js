/**
 * Created by danny on 16-9-30.
 */

Ext.define('scpc.store.BaseCapability', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.BaseCapability',
    proxy: {
        type: 'ajax',
        url: 'base_cap',
        reader: {
            type: 'json'
        }
    }
})
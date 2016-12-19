/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.SurplusCapability', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.SurplusCapability',
    proxy: {
        type: 'ajax',
        url: 'surplus.capability',
        reader: {
            type: 'json',
        }
    }
})
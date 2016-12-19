/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.CapabilityClassify', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.CapabilityClassify',
    proxy: {
        type: 'ajax',
        url: 'capability.classify',
        reader: {
            type: 'json'
        }
    }
})
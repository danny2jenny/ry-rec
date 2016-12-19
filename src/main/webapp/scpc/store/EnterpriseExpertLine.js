/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.EnterpriseExpertLine', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.EnterpriseExpert',
    proxy: {
        type: 'ajax',
        url: 'enterprise.expert',
        reader: {
            type: 'json'
        },
        extraParams: {
            type: 1
        }
    }
})
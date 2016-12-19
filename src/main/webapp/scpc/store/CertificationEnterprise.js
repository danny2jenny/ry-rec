/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.CertificationEnterprise', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.CertificationEnterprise',
    proxy: {
        type: 'ajax',
        url: 'certification.enterprise',
        reader: {
            type: 'json',
        }
    }
})
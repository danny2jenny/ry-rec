/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.BadBehaviorType', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.BadBehaviorType',
    proxy: {
        type: 'ajax',
        url: 'bad.behavior.type',
        reader: {
            type: 'json'
        }
    }
})
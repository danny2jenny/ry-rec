/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.BadBehavior', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.BadBehavior',
    proxy: {
        type: 'ajax',
        url: 'bad.behavior',
        reader: {
            type: 'json'
        }
    }
})
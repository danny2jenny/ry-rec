/**
 * Created by danny on 17-2-17.
 */

Ext.define('app.store.Actions', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    autoSync: true,
    model: 'app.model.Actions',
    proxy: {
        type: 'direct',
        api: {
            read: extActions.list,
            create: extActions.create,
            update: extActions.update,
            destroy: extActions.delete
        }
    }
});
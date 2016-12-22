/**
 * Created by danny on 16-12-22.
 */

Ext.define('ryrec.store.Option', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    autoSync: true,
    model: 'ryrec.model.Option',
    groupField: 'cat',
    proxy: {
        type: 'direct',
        api: {
            read: extOption.list,
            create: extOption.create,
            update: extOption.update,
            destroy: extOption.delete
        }
    }
})
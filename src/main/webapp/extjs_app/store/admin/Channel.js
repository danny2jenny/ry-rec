/**
 * Created by danny on 16-12-19.
 */

Ext.define('ryrec.store.admin.Channel', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    autoSync: true,
    model: 'ryrec.model.admin.Channel',
    proxy: {
        type: 'direct',
        api: {
            read: extChannel.list,
            create: extChannel.create,
            update: extChannel.update,
            destroy: extChannel.delete
        }
    }
})
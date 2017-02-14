/**
 * Created by danny on 16-12-20.
 */

Ext.define('app.store.NodeForDevice', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    autoSync: true,
    model: 'app.model.Node',
    proxy: {
        type: 'direct',
        api: {
            read: extNodeForDevice.list,
            create: extNodeForDevice.create,
            update: extNodeForDevice.update,
            destroy: extNodeForDevice.delete
        }
    }
})
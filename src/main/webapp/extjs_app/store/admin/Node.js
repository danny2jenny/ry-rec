/**
 * Created by danny on 16-12-20.
 */

Ext.define('ryrec.store.admin.Node', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    autoSync: true,
    model: 'ryrec.model.admin.Node',
    proxy: {
        type: 'direct',
        api: {
            read: extNode.list,
            create: extNode.create,
            update: extNode.update,
            destroy: extNode.delete
        }
    }
})
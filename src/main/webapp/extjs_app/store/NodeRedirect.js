Ext.define('app.store.NodeRedirect', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    autoSync: true,
    model: 'app.model.NodeRedirect',
    proxy: {
        type: 'direct',
        api: {
            read: extNodeRedirect.list,
            create: extNodeRedirect.create,
            update: extNodeRedirect.update,
            destroy: extNodeRedirect.delete
        }
    }
});
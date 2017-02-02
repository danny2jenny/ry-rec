/**
 * Created by danny on 17-1-6.
 */

Ext.define('app.store.GisLayer', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    autoSync: true,
    model: 'app.model.GisLayer',
    proxy: {
        type: 'direct',
        api: {
            read: extGisLayer.list,
            create: extGisLayer.create,
            update: extGisLayer.update,
            destroy: extGisLayer.delete
        }
    }


});
/**
 * Created by danny on 17-1-3.
 *
 * Gis 表
 */

Ext.define('app.store.Gis', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    autoSync: true,
    model: 'app.model.Gis',
    proxy: {
        type: 'direct',
        api: {
            read: extGis.list,
            create: extGis.create,
            update: extGis.update,
            destroy: extGis.delete
        }
    }
})
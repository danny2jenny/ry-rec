/**
 * Created by danny on 17-5-10.
 */

Ext.define('app.store.Panorama', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    autoSync: true,
    model: 'app.model.Panorama',
    proxy: {
        type: 'direct',
        api: {
            read: extPanorama.list,
            create: extPanorama.create,
            update: extPanorama.update,
            destroy: extPanorama.delete
        }
    }
});
/**
 * Created by danny on 17-5-10.
 */

Ext.define('app.store.PanoramaDevice', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    autoSync: true,
    model: 'app.model.PanoramaDevice',
    proxy: {
        type: 'direct',
        api: {
            read: extPanoramaDevice.list,
            create: extPanoramaDevice.create,
            update: extPanoramaDevice.update,
            destroy: extPanoramaDevice.delete
        }
    }
});
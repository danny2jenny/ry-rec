/**
 * Created by danny on 17-4-11.
 */

Ext.define('app.store.NvrNode', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    autoSync: true,
    model: 'app.model.DeviceNode',
    proxy: {
        type: 'direct',
        api: {
            read: extVideo.listNvrNode
        }
    }
});
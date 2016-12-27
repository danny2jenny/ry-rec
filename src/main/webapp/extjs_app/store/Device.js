/**
 * Created by danny on 16-12-21.
 *
 * Device 的存储
 */

Ext.define('app.store.Device', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    autoSync: true,
    model: 'app.model.Device',
    groupField: 'type',
    proxy: {
        type: 'direct',
        api: {
            read: extDevice.list,
            create: extDevice.create,
            update: extDevice.update,
            destroy: extDevice.delete
        }
    }
})

/**
 * Created by danny on 16-12-21.
 *
 * Device 的存储
 */

Ext.define('ryrec.store.Device', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    autoSync: true,
    model: 'ryrec.model.Device',
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

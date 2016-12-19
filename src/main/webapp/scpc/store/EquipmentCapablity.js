/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.EquipmentCapablity', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.EquipmentCapablity',
    proxy: {
        type: 'ajax',
        url: 'equipment.capablity',
        reader: {
            type: 'json',
        }
    }
})
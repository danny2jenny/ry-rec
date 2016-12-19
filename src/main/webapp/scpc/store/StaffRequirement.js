/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.StaffRequirement', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.StaffRequirement',
    proxy: {
        type: 'ajax',
        url: 'staff.requirement',
        reader: {
            type: 'json',
        }
    }
})
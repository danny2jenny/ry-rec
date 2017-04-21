/**
 * Created by 12793 on 2017/4/14.
 */

Ext.define('app.store.Alarm', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    autoSync: true,
    model: 'app.model.Alarm',
    groupField: 'day',
    groupDir: 'DESC',
    pageSize: 10,
    proxy: {
        type: 'direct',
        api: {
            read: extAlarm.list
        },
        reader: {
            root: 'records'
        }
    }
});



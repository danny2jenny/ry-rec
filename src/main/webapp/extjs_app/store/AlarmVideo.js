/**
 * Created by 12793 on 2017/4/20.
 */

Ext.define('app.store.AlarmVideo', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'app.model.AlarmVideo',
    proxy: {
        type: 'direct',
        api: {
            read: extAlarmVideo.list
        }
    }
});

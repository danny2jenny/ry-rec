/**
 * Created by danny on 16-12-22.
 */


Ext.define('app.store.ChannelType', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    model: 'app.model.Option',
    proxy: {
        type: 'direct',
        api: {
            read: extOption.list
        },
        extraParams:{
            cat : 1
        }
    }
})

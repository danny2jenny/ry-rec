/**
 * Created by danny on 16-12-22.
 */


Ext.define('ryrec.store.option.ChannelType', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    model: 'ryrec.model.Option',
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

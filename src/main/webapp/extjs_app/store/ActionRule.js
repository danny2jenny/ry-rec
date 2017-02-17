/**
 * Created by danny on 17-2-17.
 */


Ext.define('app.store.ActionRule', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    autoSync: true,
    model: 'app.model.ActionRule',
    proxy: {
        type: 'direct',
        api: {
            read: extActionRule.list,
            create: extActionRule.create,
            update: extActionRule.update,
            destroy: extActionRule.delete
        }
    }
});
/**
 * Created by danny on 17-4-10.
 *
 * video 通道的配置
 */

Ext.define('app.store.Nvr', {
    extend: 'Ext.data.Store',
    autoLoad: true,
    autoSync: true,
    model: 'app.model.Channel',
    proxy: {
        type: 'direct',
        api: {
            read: extVideo.listNvr
        }
    }
});
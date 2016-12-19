/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.ProjectsProgressYes', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.ProjectsProgress',
    proxy: {
        type: 'ajax',
        url: 'projects.progress',
        reader: {
            type: 'json'
        },
        extraParams: {
            progress: 1
        }
    }
})
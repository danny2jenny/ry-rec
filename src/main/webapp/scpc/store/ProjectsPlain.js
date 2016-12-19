/**
 * Created by danny on 16-10-13.
 */

Ext.define('scpc.store.ProjectsPlain', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    model: 'scpc.model.ProjectsPlain',
    proxy: {
        type: 'ajax',
        url: 'projects.plain',
        reader: {
            type: 'json'
        }
    }
})
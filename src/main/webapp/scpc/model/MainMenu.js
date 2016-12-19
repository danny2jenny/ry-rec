/**
 * Created by danny on 16-9-30.
 */

Ext.define('scpc.model.MainMenu', {
    extend: 'Ext.data.Model',
    fields: [
        /*
         id
         icon
         leaf
         parentId
         text
         url
         */
        {name: 'id', type: 'auto'},
        {
            name: 'icon',
            convert: function (value) {
                return "/static/icon/16/" + value + ".png";
            }
        },
        {
            name: 'leaf',
            convert: function (value, record) {
                ac = record.get('action');
                id = record.get('id');

                if (id > 0) {
                    l = (ac == "") ? false : true;
                } else {
                    l = false;
                }

                return l;
            }
        },
        {name: 'pid', type: 'auto'},
        {name: 'text', type: 'auto', mapping: 'name'},
        {name: 'action', type: 'auto'},
    ]
});

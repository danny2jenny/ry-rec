/**企业人员信息.
 *
 */
Ext.define('scpc.model.EnterpriseStaff', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {
            name: 'enterprise', type: 'int', sortable: true,
            convert: function (value, record) {
                return ry.trans(value, ry.Enterprise);
            }
        },

        {name: 'constructorFirst', type: 'int', sortable: true},
        {name: 'constructorSecond', type: 'int', sortable: true},
        {
            name: 'constructorAll', type: 'int', sortable: true,
            convert: function (value, record) {
                return record.get('constructorFirst') + record.get('constructorSecond');
            }
        },

        {name: 'titleMiddle', type: 'int', sortable: true},
        {name: 'titleHigh', type: 'int', sortable: true},
        {
            name: 'titleAll', type: 'int', sortable: true,
            convert: function (value, record) {
                return record.get('titleMiddle') + record.get('titleHigh');
            }
        },

        {name: 'skillMiddle', type: 'int', sortable: true},
        {name: 'skillHigh', type: 'int', sortable: true},
        {name: 'tech', type: 'int', sortable: true},
        {name: 'techHigh', type: 'int', sortable: true},
        {
            name: 'techAll', type: 'int', sortable: true,
            convert: function (value, record) {
                return record.get('skillMiddle') + record.get('skillHigh') + record.get('tech') + record.get('techHigh');
            }
        },

        {name: 'modified', type: 'string', sortable: true}
    ],
});

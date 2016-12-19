Ext.define('scpc.model.AnnualCapacity', {
    extend: 'Ext.data.Model',

    /**fields  type
     * auto (默认值, 意味着无convert方法)
     string
     int
     float
     boolean
     date
     */
    fields: [
        {name: 'id', type: 'int', sortable: true},
        {name: 'voltageRank', type: 'int', sortable: true},
        {name: 'projectType', type: 'int', sortable: true},
        {name: 'reasonablePeriod', type: 'int', sortable: true},
        {name: 'oneGroupPerYear', type: 'float', sortable: true},
        {name: 'annualCapacity', type: 'int', sortable: true},
        {name: 'memo', type: 'String', sortable: true},
        {name: 'createtime', type: 'string', sortable: true}
    ],
});

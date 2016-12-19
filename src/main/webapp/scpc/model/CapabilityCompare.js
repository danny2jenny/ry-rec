Ext.define('scpc.model.CapabilityCompare', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'supervisor', type: 'string', sortable: true},
        {name: 'name', type: 'string', sortable: true},
        {name: 'project500', type: 'int', sortable: true},
        {name: 'project220', type: 'int', sortable: true},
        {name: 'project110', type: 'int', sortable: true},
        {name: 'projectAll', type: 'int', sortable: true},
        {name: 'projectAverage', type: 'int', sortable: true},  //三年历史平均
        {name: 'projectDiff', type: 'int', sortable: true},
        {name: 'outputSound', type: 'int', sortable: true},
        {name: 'outputAverage', type: 'int', sortable: true},   //三年历史平均
        {name: 'outputDiff', type: 'int', sortable: true},

        {   //评估项目总数
            name: 'outputAll', type: 'float', sortable: true,
            convert: function (value, record) {
                var t = record.get('outputSound') / 10000;
                return t
            }
        },

        //近三年所承担的总量
        {name: 'outputSum', type: 'float', sortable: true},
        {name: 'projectSum', type: 'int', sortable: true}
    ]

});

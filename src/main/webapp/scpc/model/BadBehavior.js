
Ext.define('scpc.model.BadBehavior', {
	extend : 'Ext.data.Model',
	fields : [ 
		{name: 'id', type: 'int',	sortable : true},
		{name: 'enterprise', type: 'int',	sortable : true},
		{name: 'year', type: 'int',	sortable : true},
		{name: 'general', type: 'int',	sortable : true},
		{name: 'serious', type: 'int',	sortable : true},
		{name: 'weight', type: 'float',	sortable : true},
		{name: 'modified', type: 'string',	sortable : true}
	],
});

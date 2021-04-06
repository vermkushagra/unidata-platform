Ext.define('Unidata.model.search.SearchQuery', {
    extend: 'Unidata.model.Base',

    fields: [
        {name: 'text', type: 'string'},
        {name: 'fields', type: 'string'},
        {name: 'qtype', type: 'string', defaultValue: 'MATCH'},
        {name: 'source', type: 'boolean', defaultValue: false},
        {name: 'count', type: 'int', defaultValue: 10},
        {name: 'total_count', type: 'boolean', defaultValue: false},
        {name: 'count_only', type: 'boolean', defaultValue: false}
    ]
});

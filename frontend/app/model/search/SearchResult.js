Ext.define('Unidata.model.search.SearchResult', {
    extend: 'Unidata.model.Base',

    fields: [
        {name: 'total_count', type: 'int'},
        {name: 'fields', type: 'string'}
    ],
    hasMany: [
        {
            name: 'hits',
            model: 'search.SearchHit'
        }
    ]
});

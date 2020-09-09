Ext.define('Unidata.model.dashboard.DqerrorsDaily', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'entityName',
            type: 'string'
        },
        {
            name: 'category',
            mapping: 'dimension2',
            type: 'string'
        },
        {
            name: 'severity',
            mapping: 'dimension1',
            type: 'string'
        },
        {
            name: 'atDate',
            type: 'date'
        },
        {
            name: 'count',
            type: 'int'
        }
    ]

});

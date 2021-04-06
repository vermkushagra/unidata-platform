Ext.define('Unidata.model.dashboard.Series', {
    extend: 'Ext.data.Model',

    fields: [
        {
            name: 'time',
            type: 'string'
        },
        {
            name: 'totalCount',
            type: 'int'
        },
        {
            name: 'newCount',
            type: 'int'
        },
        {
            name: 'updatedCount',
            type: 'int'
        },
        {
            name: 'mergedCount',
            type: 'int'
        },
        {
            name: 'errorsCount',
            type: 'int'
        },
        {
            name: 'mergedErrorsCount',
            type: 'int'
        }
    ]
});

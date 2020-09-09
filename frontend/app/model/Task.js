Ext.define('Unidata.model.Task', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'assigner',
            type: 'string'
        },
        {
            name: 'creation_date',
            type: 'string'
        },
        {
            name: 'deadline',
            type: 'string'
        },
        {
            name: 'lookup',
            type: 'string'
        },
        {
            name: 'priority',
            type: 'number'
        },
        {
            name: 'record',
            type: 'string'
        },
        {
            name: 'companyCode',
            type: 'string'
        },
        {
            name: 'companyCode',
            type: 'string'
        }
    ]
});

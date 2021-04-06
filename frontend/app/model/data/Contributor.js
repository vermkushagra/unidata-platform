Ext.define('Unidata.model.data.Contributor', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'originId',
            type: 'string',
            unique: true
        },
        {
            name: 'version',
            type: 'int',
            unique: true
        },
        {
            name: 'sourceSystem',
            type: 'string'
        },
        {
            name: 'status',
            type: 'string'
        },
        {
            name: 'owner',
            type: 'string'
        },
        {
            name: 'approval',
            type: 'string'
        }
    ]
});

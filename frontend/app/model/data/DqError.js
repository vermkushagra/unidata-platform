Ext.define('Unidata.model.data.DqError', {
    extend: 'Unidata.model.Base',

    idProperty: 'ruleName',

    fields: [
        {
            name: 'severity',
            type: 'string'
        },
        {
            name: 'category',
            type: 'string'
        },
        {
            name: 'message',
            type: 'string'
        },
        {
            name: 'ruleName',
            type: 'string'
        },
        {
            name: 'phase',
            type: 'string',
            allowNull: true
        }
    ]
});

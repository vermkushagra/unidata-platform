Ext.define('Unidata.model.KeyValuePair', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {
            name: 'tempId',
            type: 'auto'
        },
        {
            name: 'name',
            type: 'string',
            validators: [
                {
                    type: 'presence'
                }
            ]
        },
        {
            name: 'value',
            type: 'string'
        }
    ]

});

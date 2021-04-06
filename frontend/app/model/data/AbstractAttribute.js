Ext.define('Unidata.model.data.AbstractAttribute', {
    extend: 'Unidata.model.Base',

    idProperty: 'name',

    fields: [
        {
            name: 'name',
            type: 'string'
        }
    ],

    validators: {
        name: [
            {
                type: 'format', matcher: /([A-Za-z]+)[0-9]/i
            }
        ]
    }
});

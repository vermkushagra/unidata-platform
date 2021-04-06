Ext.define('Unidata.model.attribute.AbstractSimpleAttribute', {
    extend: 'Unidata.model.attribute.AbstractAttribute',

    fields: [
        {
            name: 'searchable',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'displayable',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'mainDisplayable',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'mask',
            type: 'string',
            defaultValue: null
        }
    ],

    hasMany: [
        {
            name: 'customProperties',
            model: 'KeyValuePair'
        }
    ]

});

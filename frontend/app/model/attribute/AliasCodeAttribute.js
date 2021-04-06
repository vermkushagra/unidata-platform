Ext.define('Unidata.model.attribute.AliasCodeAttribute', {
    extend: 'Unidata.model.attribute.CodeAttribute',

    fields: [
        {
            name: 'nullable',
            type: 'boolean',
            defaultValue: true
        }
    ],

    hasMany: [
        {
            name: 'customProperties',
            model: 'KeyValuePair'
        }
    ]

});

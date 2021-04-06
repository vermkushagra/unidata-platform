Ext.define('Unidata.model.attribute.ComplexAttribute', {
    extend: 'Unidata.model.attribute.AbstractAttribute',

    fields: [
        {
            name: 'nestedEntityType',
            type: 'string'
        },
        {
            name: 'minCount',
            type: 'int'
        },
        {
            name: 'maxCount',
            type: 'int',
            allowNull: true
        },
        {
            name: 'subEntityKeyAttribute',
            type: 'string'
        }
    ],

    hasOne: [{
        name: 'nestedEntity',
        model: 'entity.NestedEntity'
    }],

    hasMany: [
        {
            name: 'customProperties',
            model: 'KeyValuePair'
        }
    ]

});

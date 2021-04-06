Ext.define('Unidata.model.entity.NestedEntity', {
    extend: 'Unidata.model.entity.AbstractEntity',

    //idProperty: 'id',

    hasMany: [
        {
            name: 'simpleAttributes',
            model: 'attribute.SimpleAttribute'
        },
        {
            name: 'complexAttributes',
            model: 'attribute.ComplexAttribute'
        },
        {
            name: 'arrayAttributes',
            model: 'attribute.ArrayAttribute'
        },
        {
            name: 'customProperties',
            model: 'KeyValuePair'
        }
    ]
});

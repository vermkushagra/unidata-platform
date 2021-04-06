Ext.define('Unidata.model.data.NestedRecord', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false}
    ],

    hasMany: [
        {
            name: 'simpleAttributes',
            model: 'data.SimpleAttribute'
        },
        {
            name: 'complexAttributes',
            model: 'data.ComplexAttribute'
        },
        {
            name: 'arrayAttributes',
            model: 'data.ArrayAttribute'
        }
    ]
});

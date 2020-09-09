Ext.define('Unidata.model.data.OriginRecord', {
    extend: 'Unidata.model.data.AbstractRecord',

    idProperty: 'originId',

    fields: [
        {
            name: 'originId',
            type: 'string'
        }
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
            name: 'dqErrors',
            model: 'data.DqError'
        },
        {
            name: 'classifiers',
            model: 'data.ClassifierNode'
        },
        {
            name: 'arrayAttributes',
            model: 'data.ArrayAttribute'
        },
        {
            name: 'codeAttributes',
            model: 'data.CodeAttribute'
        }
    ]

});
//TODO: SS rename OriginRecord to Origin ?

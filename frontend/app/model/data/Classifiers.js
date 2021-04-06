Ext.define('Unidata.model.data.Classifiers', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {
            name: 'classifierName',
            type: 'string'
        },
        {
            name: 'classifierNodeId',
            type: 'string',
            allowNull: true
        },
        {
            name: 'etalonId',
            type: 'string',
            allowNull: true
        }
    ],

    hasMany: [
        {
            name: 'simpleAttributes',
            model: 'data.SimpleAttribute'
        }
    ]
});

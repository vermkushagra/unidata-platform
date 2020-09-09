/**
 * Модель узла классификации записи
 */
Ext.define('Unidata.model.data.ClassifierNode', {
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
        },
        {
            name: 'arrayAttributes',
            model: 'data.ArrayAttribute'
        }
    ]
});

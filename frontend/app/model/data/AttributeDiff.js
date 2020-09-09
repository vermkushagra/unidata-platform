Ext.define('Unidata.model.data.AttributeDiff', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.data.SimpleAttribute',
        'Unidata.model.data.ComplexAttribute',
        'Unidata.model.data.CodeAttribute',
        'Unidata.model.data.ArrayAttribute'
    ],

    fields: [
        {
            name: 'path',
            type: 'string'
        },
        {
            name: 'action',
            type: 'string'
        }
    ],
    hasOne: [
        {
            name: 'oldSimpleValue',
            model: 'data.SimpleAttribute'
        },
        {
            name: 'oldComplexValue',
            model: 'data.ComplexAttribute'
        },
        {
            name: 'oldCodeValue',
            model: 'data.CodeAttribute'
        },
        {
            name: 'oldArrayValue',
            model: 'data.ArrayAttribute'
        }
    ]
});

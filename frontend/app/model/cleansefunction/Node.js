Ext.define('Unidata.model.cleansefunction.Node', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    required: ['Unidata.model.data.SimpleAttribute'],

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'nodeId',
            type: 'int'
        },
        {
            name: 'nodeType',
            type: 'string'
        },
        {
            name: 'functionName',
            type: 'string'
        },
        {
            name: 'uiRelativePosition',
            type: 'string'
        }
    ],

    hasOne: [
        {
            name: 'value',
            model: 'data.SimpleAttribute'
        }
    ]
});

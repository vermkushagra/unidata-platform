Ext.define('Unidata.model.cleansefunction.Node', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

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
    ]

});

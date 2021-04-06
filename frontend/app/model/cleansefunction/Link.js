Ext.define('Unidata.model.cleansefunction.Link', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'fromNodeId',
            type: 'int'
        },
        {
            name: 'fromPort',
            type: 'string'
        },
        {
            name: 'toNodeId',
            type: 'int'
        },
        {
            name: 'toPort',
            type: 'string'
        }
    ]

});

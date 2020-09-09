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
            name: 'fromPortType',
            type: 'string',
            defaultValue: Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT
        },
        {
            name: 'toNodeId',
            type: 'int'
        },
        {
            name: 'toPort',
            type: 'string'
        },
        {
            name: 'toPortType',
            type: 'string',
            defaultValue: Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT
        }
    ]

});

Ext.define('Unidata.model.dataquality.Output', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'attributeName',
            type: 'string'
        },
        {
            name: 'functionPort',
            type: 'string'
        }
    ]
});

Ext.define('Unidata.model.dataquality.Input', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'attributeName',
            type: 'string',
            allowNull: true,
            defaultValue: null
        },
        {
            name: 'functionPort',
            type: 'string'
        },
        {
            name: 'attributeConstantValue',
            type: 'auto',
            allowNull: true,
            defaultValue: null
        }
    ]
});

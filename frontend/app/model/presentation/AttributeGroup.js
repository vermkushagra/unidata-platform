Ext.define('Unidata.model.presentation.AttributeGroup', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'row',
            type: 'int'
        },
        {
            name: 'column',
            type: 'int'
        },
        {
            name: 'attributes'
        }
    ]
});

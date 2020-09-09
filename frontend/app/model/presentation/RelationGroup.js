Ext.define('Unidata.model.presentation.RelationGroup', {
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
            type: 'int',
            default: 0
        },
        {
            name: 'relType',
            type: 'string'
        },
        {
            name: 'relations'
        }
    ]
});

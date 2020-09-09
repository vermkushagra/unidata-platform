Ext.define('Unidata.model.matching.MatchingField', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'constantField',
            type: 'boolean'
        }
    ]
});

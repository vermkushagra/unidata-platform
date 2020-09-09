Ext.define('Unidata.model.matching.MatchingAlgorithm', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.matching.MatchingField'
    ],

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
        }
    ],

    hasMany: [
        {
            name: 'matchingFields',
            model: 'matching.MatchingField'
        }
    ]
});

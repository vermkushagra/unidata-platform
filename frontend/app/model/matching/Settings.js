Ext.define('Unidata.model.matching.Settings', {
    extend: 'Unidata.model.Base',

    idProperty: 'id',

    /*отрицательные идентификаторы для новых записей*/
    identifier: 'negative',

    fields: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'groupId',
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
            name: 'order',
            type: 'int'
        }
    ],

    hasMany: [
        {
            name: 'matchFields',
            model: 'matching.MatchField'
        }
    ]
});

Ext.define('Unidata.model.matching.PostAction', {
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
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        }
    ],

    proxy: {
        url: Unidata.Config.getMainUrl() + 'internal/matching/postActions/',
        reader: {
            type: 'json'
        }
    }
});

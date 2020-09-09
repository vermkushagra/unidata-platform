Ext.define('Unidata.model.matching.Group', {
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
        },
        {
            name: 'autoMerge',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'entityName',
            type: 'string'
        },
        {
            name: 'postActionId',
            type: 'int'
        },
        {
            name: 'ruleIds'
        }
    ],

    proxy: {
        type: 'rest',
        url: Unidata.Config.getMainUrl() + 'internal/matching/group/',
        writer: {
            type: 'json',
            writeAllFields: true,
            allDataOptions: {
                persist: true,
                associated: true
            }
        },
        reader: {
            type: 'json',
            rootProperty: 'content'
        }
    }
});

Ext.define('Unidata.model.entity.Relation', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'fromEntity',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'toEntity',
            type: 'string'
        },
        {
            name: 'relType',
            type: 'string'
        },
        {
            name: 'toEntityDefaultDisplayAttributes',
            type: 'auto'
        },
        {
            name: 'useAttributeNameForDisplay',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'required',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'hasData',
            type: 'boolean',
            defaultValue: false
        }
    ],

    validators: {
        name: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required', {context: 'includeFieldName', fieldName: Unidata.i18n.t('glossary:relationName')})
            },
            {
                type: 'latinalphanumber'
            }
        ],
        displayName: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required', {context: 'includeFieldName', fieldName: Unidata.i18n.t('glossary:relationDisplayName')})
            }
        ],
        toEntity: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required', {context: 'includeFieldName', fieldName: Unidata.i18n.t('glossary:linkedEntity')})
            }
        ],
        // Поле "Вложенная сущность" временно не используется
        //toEntityDefaultDisplayAttribute: [
        //    {
        //        type: 'presence',
        //        message: 'Поле "Вложенная сущность" должно быть заполнено'
        //    }
        //],
        relType: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required', {context: 'includeFieldName', fieldName: Unidata.i18n.t('glossary:relationType')})
            }
        ]
    },

    hasMany: [
        {
            name: 'simpleAttributes',
            model: 'attribute.SimpleAttribute',
            storeConfig: {
                /*
                 * Блокируем загрузку store
                 * В случае если backend пришлет null вместо пустого массива ExtJS запрашивает данные
                 * по url с именем модели
                 *
                 * https://unidata.atlassian.net/browse/UN-1062
                 * https://www.sencha.com/forum/showthread.php?302601-Nested-Model-Data-Bind-resulting-in-server-request-for-data
                 */
                load: function () {
                    return;
                }
            }
        },
        {
            name: 'customProperties',
            model: 'KeyValuePair',
            storeConfig: {
                /*
                 * Блокируем загрузку store
                 * В случае если backend пришлет null вместо пустого массива ExtJS запрашивает данные
                 * по url с именем модели
                 *
                 * https://unidata.atlassian.net/browse/UN-1062
                 * https://www.sencha.com/forum/showthread.php?302601-Nested-Model-Data-Bind-resulting-in-server-request-for-data
                 */
                load: function () {
                    return;
                }
            }
        }
    ]
});

Ext.define('Unidata.model.user.Role', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'type',
            type: 'string'
        }
    ],

    hasMany: [
        {
            name: 'rights',
            model: 'user.Right',
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
            name: 'securityLabels',
            model: 'user.SecurityLabelRole'
        },
        {
            name: 'properties',
            model: 'user.RoleProperty'
        }
    ],

    validators: {
        name: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required')
            },
            {
                type: 'latinalphanumber'
            }
        ],
        displayName: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required')
            }
        ]
    },

    proxy: {
        type: 'rest',
        url: Unidata.Config.getMainUrl() + 'internal/security/role/',
        writer: {
            type: 'json',
            writeAllFields: true,
            writeRecordId: false
        },
        reader: {
            type: 'json',
            rootProperty: 'content'
        }
    }
});

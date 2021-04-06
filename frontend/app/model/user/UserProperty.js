Ext.define('Unidata.model.user.UserProperty', {
    extend: 'Unidata.model.Base',

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
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'value',
            type: 'string'
        }
    ],

    proxy: {
        type: 'rest.extended',

        limitParam: '',
        startParam: '',
        pageParam: '',

        paramsAsJson: true,

        reader: {
            type: 'json',
            rootProperty: 'content'
        },

        writer: {
            type: 'json',
            writeAllFields: true,
            rootProperty: ''
        },

        baseUrl: Unidata.Config.getMainUrl(),

        url: 'internal/security/user/user-properties',

        api: {
            create:  'internal/security/user/user-properties',
            read:    'internal/security/user/user-properties/list',
            update:  'internal/security/user/user-properties/{id}',
            destroy: 'internal/security/user/user-properties/{id}'
        },

        actionMethods: {
            create:  'PUT',
            read:    'GET',
            update:  'PUT',
            destroy: 'DELETE'
        }
    }
});

Ext.define('Unidata.model.user.UserEndpoint', {
    extend: 'Unidata.model.Base',

    idProperty: 'name',

    fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'description',
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

        url: 'internal/security/user/user-api',

        api: {
            create:  'internal/security/user/user-api',
            read:    'internal/security/user/user-api/list',
            update:  'internal/security/user/user-api/{name}',
            destroy: 'internal/security/user/user-api/{name}'
        },

        actionMethods: {
            create:  'PUT',
            read:    'GET',
            update:  'PUT',
            destroy: 'DELETE'
        }
    }

});

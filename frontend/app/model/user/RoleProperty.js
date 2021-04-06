Ext.define('Unidata.model.user.RoleProperty', {
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

        url: 'internal/security/role/role-properties',

        api: {
            create:  'internal/security/role/role-properties',
            read:    'internal/security/role/role-properties/list',
            update:  'internal/security/role/role-properties/{id}',
            destroy: 'internal/security/role/role-properties/{id}'
        },

        actionMethods: {
            create:  'PUT',
            read:    'GET',
            update:  'PUT',
            destroy: 'DELETE'
        }
    }
});

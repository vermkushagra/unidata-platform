Ext.define('Unidata.model.user.User', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.user.UserProperty'
    ],

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'active',
            type: 'boolean'
        },
        {
            name: 'admin',
            type: 'boolean'
        },
        {
            name: 'login',
            type: 'string'
        },
        {
            name: 'email',
            type: 'string'
        },
        {
            name: 'firstName',
            type: 'string'
        },
        {
            name: 'fullName',
            type: 'string'
        },
        {
            name: 'lastName',
            type: 'string'
        },
        'roles'
    ],

    hasMany: [
        {
            name: 'securityLabels',
            model: 'user.SecurityLabelUser'
        },
        {
            name: 'endpoints',
            model: 'user.UserEndpoint'
        },
        {
            name: 'properties',
            model: 'user.UserProperty'
        }
    ],

    validators: {
        login: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required')
            },
            {
                type: 'latinalphanumber'
            }
        ]
    },

    proxy: {
        type: 'rest',
        url: Unidata.Config.getMainUrl() + 'internal/security/user',
        writer: {
            type: 'json',
            writeAllFields: true
        },
        reader: {
            type: 'json',
            rootProperty: 'content'
        }
    }
});

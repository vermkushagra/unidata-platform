Ext.define('Unidata.model.user.SecurityLabelRole', {
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
            name: 'description',
            type: 'string'
        }
    ],

    hasMany: [
        {
            name: 'attributes',
            model: 'user.SecurityLabelAttributeRole'
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
        ]
    },

    proxy: {
        type: 'rest',
        url: Unidata.Config.getMainUrl() + 'internal/security/label/',
        writer: {
            type: 'json',
            writeAllFields: true,
            writeRecordId: false
        },
        reader: {
            type: 'json'
            //rootProperty: 'content'
        }
    }
});

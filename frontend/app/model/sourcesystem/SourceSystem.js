Ext.define('Unidata.model.sourcesystem.SourceSystem', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'weight',
            type: 'int',
            defaultValue: 0
        }
    ],

    hasMany: [
        {
            name: 'customProperties',
            model: 'KeyValuePair'
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

        url: Unidata.Config.getMainUrl() + 'internal/meta/source-systems',

        writer: {
            type: 'json',
            allDataOptions: {
                persist: true,
                associated: true
            },
            writeAllFields: true
        }
    },

    statics: {
        createTypeIcon: function () {
            return '<i class="fa fa-indent"></i>';
        }
    }
});

Ext.define('Unidata.model.entity.Enumeration', {
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
        }
    ],

    hasMany: [
        {
            name: 'values',
            model: 'attribute.Enumeration'
        }
    ],

    validators: {
        name: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required', {context: 'includeFieldName', fieldName: Unidata.i18n.t('glossary:name')})
            },
            {
                type: 'latinalphanumber'
            }
        ],
        displayName: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required', {context: 'includeFieldName', fieldName: Unidata.i18n.t('glossary:displayName')})
            }
        ]
    }
});

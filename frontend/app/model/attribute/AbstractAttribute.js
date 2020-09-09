Ext.define('Unidata.model.attribute.AbstractAttribute', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.KeyValuePair'
    ],

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
        },
        {
            name: 'readOnly',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'hidden',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'order',
            type: 'int',
            required: true
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
    },

    // устанавливаем значения по умолчанию для типов

    isSimpleDataType: function () {
        return false;
    },

    isLookupEntityType: function () {
        return false;
    },

    isEnumDataType: function () {
        return false;
    },

    isLinkDataType: function () {
        return false;
    },

    isSimpleMeasurementDataType: function () {
        return false;
    },

    isArrayDataType: function () {
        return false;
    },

    isClassifierNodeAttribute: function () {
        return false;
    },

    isBlobOrClobAttribute: function () {
        return false;
    }
});

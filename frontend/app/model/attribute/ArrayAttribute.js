Ext.define('Unidata.model.attribute.ArrayAttribute', {
    extend: 'Unidata.model.attribute.AbstractAttribute',

    fields: [
        {
            name: 'searchable',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'displayable',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'mainDisplayable',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'mask',
            type: 'string',
            defaultValue: null
        },
        {
            name: 'nullable',
            type: 'boolean',
            defaultValue: true
        },
        {
            name: 'unique',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'arrayDataType',
            type: 'string'
        },
        {
            name: 'lookupEntityCodeAttributeType',
            type: 'string'
        },
        {
            name: 'exchangeSeparator',
            type: 'string',
            defaultValue: '|'
        },
        {
            name: 'typeCategory',
            convert: function (value, record) {
                var type = '';

                if (Boolean(record.get('lookupEntityType'))) {
                    type = 'lookupEntityType';
                } else if (Boolean(record.get('arrayDataType'))) {
                    type = 'arrayDataType';
                }

                return type;
            },

            depends: ['arrayDataType', 'lookupEntityType'],
            persist: false
        },
        {
            name: 'typeValue',

            /*здесь необходимо использовать convert вместо calculate т.к. зависимые данные могут изменяться*/
            convert: function (v, record) {
                return record.get(record.get('typeCategory'));
            },

            depends: ['typeCategory', 'arrayDataType', 'lookupEntityType'],

            persist: false
        },
        {
            name: 'lookupEntityType',
            type: 'string'
        },
        {
            name: 'lookupEntityDisplayAttributes',
            type: 'auto',
            defaultValue: []
        },
        {
            name: 'lookupEntitySearchAttributes',
            type: 'auto',
            defaultValue: []
        },
        {
            name: 'useAttributeNameForDisplay',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'searchMorphologically',
            type: 'boolean',
            defaultValue: false
        }
    ],

    hasMany: [
        {
            name: 'customProperties',
            model: 'KeyValuePair'
        }
    ],

    validators: {
        typeValue: [
            {
                type: 'presence',
                message: Unidata.i18n.t('model>attribute.typeShouldSpecified')
            }
        ]
    },

    isArrayDataType: function () {
        return true;
    }

});

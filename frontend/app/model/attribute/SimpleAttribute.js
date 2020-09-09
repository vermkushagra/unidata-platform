Ext.define('Unidata.model.attribute.SimpleAttribute', {
    extend: 'Unidata.model.attribute.AbstractSimpleAttribute',

    fields: [
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
            name: 'simpleDataType',
            type: 'string'
        },
        {
            name: 'enumDataType',
            type: 'string'
        },
        {
            name: 'lookupEntityType',
            type: 'string'
        },
        {
            name: 'linkDataType',
            type: 'string'
        },
        {
            name: 'lookupEntityCodeAttributeType',
            type: 'string'
        },
        {
            name: 'valueId', // измеряемая величина
            allowNull: true,
            type: 'string'
        },
        {
            name: 'defaultUnitId', // единица измерения по умолчанию (измеряемой величины)
            allowNull: true,
            type: 'string'
        },
        {
            name: 'typeCategory',
            convert: function (value, record) {
                var type = '',
                    allowedValues;

                allowedValues = [
                    'simpleDataType',
                    'enumDataType',
                    'lookupEntityType',
                    'linkDataType'
                ];

                if (Ext.Array.contains(allowedValues, value))  {
                    return value;
                }

                if (Boolean(record.get('simpleDataType'))) {
                    type = 'simpleDataType';
                } else if (Boolean(record.get('enumDataType'))) {
                    type = 'enumDataType';
                } else if (Boolean(record.get('lookupEntityType'))) {
                    type = 'lookupEntityType';
                } else if (Boolean(record.get('linkDataType'))) {
                    type = 'linkDataType';
                }

                return type;
            },

            depends: ['simpleDataType', 'enumDataType', 'lookupEntityType',  'linkDataType'],
            persist: false
        },
        {
            name: 'typeValue',

            /*здесь необходимо использовать convert вместо calculate т.к. зависимые данные могут изменяться*/
            convert: function (v, record) {
                return record.get(record.get('typeCategory'));
            },

            depends: ['typeCategory', 'simpleDataType', 'enumDataType', 'lookupEntityType',  'linkDataType'],

            persist: false
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

    statics: {
        getTypesList: function () {
            return [
                {
                    name: Unidata.i18n.t('glossary:simpleDataType'),
                    value: 'simpleDataType'
                },
                {
                    name: Unidata.i18n.t('glossary:lookupEntityLink'),
                    value: 'lookupEntityType'
                },
                {
                    name: Unidata.i18n.t('glossary:enum'),
                    value: 'enumDataType'
                },
                {
                    name: Unidata.i18n.t('glossary:urlLink'),
                    value: 'linkDataType'
                }
            ];
        },

        getArrayTypesList: function () {
            return [
                {
                    name: Unidata.i18n.t('glossary:simpleDataType'),
                    value: 'arrayDataType'
                },
                {
                    name: Unidata.i18n.t('glossary:lookupEntityLink'),
                    value: 'lookupEntityType'
                }
            ];
        },

        createTypeIcon: function () {
            var faType = 'fa-file-o';

            return '<i class="fa ' + faType + '"></i> ';
        }
    },

    isDataTypeCategory: function (dataTypeCategory) {
        return this.get('typeCategory') === dataTypeCategory;
    },

    isSimpleDataType: function () {
        return this.isDataTypeCategory('simpleDataType');
    },

    isLookupEntityType: function () {
        return this.isDataTypeCategory('lookupEntityType');
    },

    isEnumDataType: function () {
        return this.isDataTypeCategory('enumDataType');
    },

    isLinkDataType: function () {
        return this.isDataTypeCategory('linkDataType');
    },

    isSimpleMeasurementDataType: function () {
        return this.isDataTypeCategory('simpleDataType') && this.get('valueId');
    },

    isBlobOrClobAttribute: function () {
        return this.isSimpleDataType() &&
            this.get('simpleDataType') === 'Blob' || this.get('simpleDataType') === 'Clob';
    },

    validators: {
        typeValue: [
            {
                type: 'presence',
                message: Unidata.i18n.t('model>attribute.typeShouldSpecified')
            }
        ]
    }
});

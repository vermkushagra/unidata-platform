Ext.define('Unidata.model.attribute.CodeAttribute', {
    extend: 'Unidata.model.attribute.AbstractSimpleAttribute',

    requires: [
        'Unidata.model.entity.GenerationStrategy'
    ],

    fields: [
        {
            name: 'nullable',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'unique',
            type: 'boolean',
            defaultValue: true
        },
        {
            name: 'displayable',
            type: 'boolean',
            defaultValue: true
        },
        {
            name: 'mainDisplayable',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'searchable',
            type: 'boolean',
            defaultValue: true
        },
        {
            name: 'simpleDataType',
            type: 'string'
        },
        {
            name: 'typeCategory',
            calculate: function () {
                // for codeAttribute typeCategory is always simpleDataType
                return 'simpleDataType';
            },
            persist: false
        },
        {
            name: 'typeValue',

            /*здесь необходимо использовать convert вместо calculate т.к. зависимые данные могут изменяться*/
            convert: function (v, rec) {
                return rec.get(rec.get('typeCategory'));
            },

            depends: ['typeCategory', 'simpleDataType'],

            persist: false
        }
    ],

    hasMany: [
        {
            name: 'customProperties',
            model: 'KeyValuePair'
        }
    ],

    isSimpleDataType: function () {
        return true;
    },

    validators: {
        simpleDataType: [
            {
                type: 'presence',
                message: Unidata.i18n.t('model>attribute.typeShouldSpecified')
            }
        ]
    },

    hasOne: [
        {
            name: 'externalIdGenerationStrategy',
            model: 'entity.GenerationStrategy'
        }
    ],

    getIdGenerationStrategyType: function () {
        var type,
            idGenerationStrategy = this.getExternalIdGenerationStrategy();

        if (idGenerationStrategy) {
            type = idGenerationStrategy.get('@type');
        }

        type = type || Unidata.model.entity.GenerationStrategy.generationStrategyType.NONE.value;

        return type;
    }
});

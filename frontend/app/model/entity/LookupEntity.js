Ext.define('Unidata.model.entity.LookupEntity', {
    extend: 'Unidata.model.entity.AbstractEntity',

    requires: [
        'Unidata.model.presentation.AttributeGroup',
        'Unidata.model.entity.GenerationStrategy'
    ],

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
        }
    ],

    hasOne: [
        {
            name: 'codeAttribute',
            model: 'attribute.CodeAttribute'
        },
        {
            name: 'mergeSettings',
            model: 'mergesettings.MergeSettings'
        },
        {
            name: 'externalIdGenerationStrategy',
            model: 'entity.GenerationStrategy'
        }
    ],

    hasMany: [
        {
            name: 'simpleAttributes',
            model: 'attribute.SimpleAttribute'
        },
        {
            name: 'aliasCodeAttributes',
            model: 'attribute.AliasCodeAttribute'
        },
        {
            name: 'arrayAttributes',
            model: 'attribute.ArrayAttribute'
        },
        {
            name: 'dataQualityRules',
            model: 'dataquality.DqRule',
            deepDirty: true
        },
        {
            name: 'attributeGroups',
            model: 'presentation.AttributeGroup'
        },
        {
            name: 'entityDependency',
            model: 'entity.EntityDependency'
        },
        {
            name: 'customProperties',
            model: 'KeyValuePair'
        }
    ],

    proxy: {
        type: 'rest',
        url: Unidata.Config.getMainUrl() + 'internal/meta/lookup-entities/',
        limitParam: 'size',
        writer: {
            type: 'json',
            writeAllFields: true,
            writeRecordId: false,
            transform: {
                fn: function (data) {
                    var GenerationStrategy = Unidata.model.entity.GenerationStrategy,
                        externalIdGenerationStrategy,
                        codeAttributeGenerationStrategy;

                    if (!data) {
                        return data;
                    }

                    // эту association отправлять на сервер не надо
                    delete data.entityDependency;

                    externalIdGenerationStrategy = data.externalIdGenerationStrategy;

                    if (Ext.isObject(externalIdGenerationStrategy)) {
                        if (externalIdGenerationStrategy['@type'] === GenerationStrategy.generationStrategyType.NONE.value) {
                            data.externalIdGenerationStrategy = null;
                        }
                    }

                    if (data.codeAttribute) {
                        codeAttributeGenerationStrategy = data.codeAttribute.externalIdGenerationStrategy;

                        if (Ext.isObject(codeAttributeGenerationStrategy)) {
                            // если кодовый атрибут не строковый или тип стратегии NONE, то присваиваем externalIdGenerationStrategy значение null
                            if (data.codeAttribute.simpleDataType !== 'String' ||
                                codeAttributeGenerationStrategy['@type'] === GenerationStrategy.generationStrategyType.NONE.value) {
                                data.codeAttribute.externalIdGenerationStrategy = null;
                            }
                        }
                    }

                    return data;
                }
            }
        },
        reader: {
            type: 'json',
            rootProperty: 'content'
        }
    },

    getMainDisplayableAttribute: function () {

        var result = null;

        function findInSimpleAttributes () {
            return this.simpleAttributes().findRecord('mainDisplayable', true);
        }

        function findInAliasCodeAttributes () {
            return this.aliasCodeAttributes().findRecord('mainDisplayable', true);
        }

        function findInCodeAttribute () {
            var attribute = null;

            if (this.getCodeAttribute().get('mainDisplayable')) {
                attribute = this.getCodeAttribute();
            }

            return attribute;
        }

        result = findInCodeAttribute() || findInAliasCodeAttributes() || findInSimpleAttributes();

        return result;
    }
});

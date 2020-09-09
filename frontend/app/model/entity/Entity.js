Ext.define('Unidata.model.entity.Entity', {
    extend: 'Unidata.model.entity.AbstractEntity',

    requires: [
        'Unidata.model.mergesettings.MergeSettings',
        'Unidata.model.presentation.AttributeGroup',
        'Unidata.model.presentation.RelationGroup',
        'Unidata.model.entity.GenerationStrategy',
        'Unidata.model.KeyValuePair'
    ],

    hasOne: [
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
            name: 'complexAttributes',
            model: 'attribute.ComplexAttribute'
        },
        {
            name: 'arrayAttributes',
            model: 'attribute.ArrayAttribute'
        },
        {
            name: 'relations',
            model: 'entity.Relation'
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
            name: 'relationGroups',
            model: 'presentation.RelationGroup'
        },
        {
            name: 'customProperties',
            model: 'KeyValuePair'
        }
    ],

    proxy: {
        type: 'rest',
        url: Unidata.Config.getMainUrl() + 'internal/meta/entities/',
        limitParam: 'size',
        writer: {
            type: 'json',
            writeAllFields: true,
            writeRecordId: false,
            transform: {
                fn: function (data) {
                    var GenerationStrategy = Unidata.model.entity.GenerationStrategy,
                        externalIdGenerationStrategy,
                        externalIdGenerationStrategyType;

                    if (!data) {
                        return data;
                    }

                    externalIdGenerationStrategy = data.externalIdGenerationStrategy;

                    // эту association отправлять на сервер не надо
                    delete data.entityDependency;

                    if (Ext.isObject(externalIdGenerationStrategy)) {
                        externalIdGenerationStrategyType = externalIdGenerationStrategy['@type'];

                        if (externalIdGenerationStrategyType === GenerationStrategy.generationStrategyType.NONE.value) {
                            data.externalIdGenerationStrategy = null;
                        } else {
                            if (externalIdGenerationStrategyType !== GenerationStrategy.generationStrategyType.CONCAT.value) {
                                delete data.externalIdGenerationStrategy.separator;
                                delete data.externalIdGenerationStrategy.attributes;
                            }

                            if (externalIdGenerationStrategyType !== GenerationStrategy.generationStrategyType.CUSTOM.value) {
                                delete data.externalIdGenerationStrategy.strategyId;
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

    /**
     * Получить relationGroup для типа связи
     * @param relType
     * @returns {*}
     */
    getRelationGroupByType: function (relType) {
        var relationGroups = this.relationGroups(),
            found,
            index;

        index = relationGroups.findExact('relType', relType);

        if (index > -1) {
            found = relationGroups.getAt(index);
        }

        return found;
    },

    /**
     * Вернуть связи для реестров которых есть права на чтение
     * @returns {Unidata.model.entity.Relation[]}
     */
    getRelationsFilteredByPermission: function () {
        var relations;

        relations = this.relations().getRange();
        relations = this.filterRelationsByPermission(relations);

        return relations;
    },

    /**
     * Отфильтровать реестры в соответствии с правами
     *
     * @param relations {Unidata.model.entity.Relation[]}
     * @returns {Unidata.model.entity.Relation[]}
     */
    filterRelationsByPermission: function (relations) {
        return Ext.Array.filter(relations, function (relation) {
            var entityName = relation.get('toEntity');

            return Unidata.Config.userHasRight(entityName, 'read');
        }, this);
    },

    /**
     * Получить метасвязи типа "Ссылка"
     * @return {Unidata.model.entity.Relation}
     */
    getReferenceRelations: function () {
        return this.getRelations(Unidata.util.MetaRecord.REL_TYPE.REFERENCES);
    },

    /**
     * Получить метасвязи типа "Включение"
     * @return {Unidata.model.entity.Relation}
     */
    getContainsRelations: function () {
        return this.getRelations(Unidata.util.MetaRecord.REL_TYPE.CONTAINS);
    },

    /**
     * Получить метасвязи типа "Многие-ко-многим"
     * @return {Unidata.model.entity.Relation}
     */
    getManyToManyRelations: function () {
        return this.getRelations(Unidata.util.MetaRecord.REL_TYPE.MANY_TO_MANY);
    },

    /**
     * Получить метасвязи соответствующего типа
     * @param relType Тип связи
     * @return {Unidata.model.entity.Relation}
     */
    getRelations: function (relType) {
        return this.relations().query('relType', relType, false, false, true);
    }
});

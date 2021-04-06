Ext.define('Unidata.model.entity.Entity', {
    extend: 'Unidata.model.entity.AbstractEntity',

    requires: [
        'Unidata.model.mergesettings.MergeSettings',
        'Unidata.model.presentation.AttributeGroup',
        'Unidata.model.presentation.RelationGroup',
        'Unidata.model.entity.GenerationStrategy'
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
            model: 'dataquality.DqRule'
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
                        externalIdGenerationStrategy;

                    if (!data) {
                        return data;
                    }

                    externalIdGenerationStrategy = data.externalIdGenerationStrategy;

                    // эту association отправлять на сервер не надо
                    delete data.entityDependency;

                    if (Ext.isObject(externalIdGenerationStrategy)) {
                        if (externalIdGenerationStrategy['@type'] === GenerationStrategy.generationStrategyType.NONE.value) {
                            data.externalIdGenerationStrategy = null;
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
    }
});

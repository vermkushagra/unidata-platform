/**
 * Утилитный класс для работы с моделью записи
 *
 * @author Ivan Marshalkin
 * @date 2016-03-16
 */

Ext.define('Unidata.util.MetaRecord', {

    singleton: true,

    TYPE_LOOKUP: 'LookupEntity',
    TYPE_ENTITY: 'Entity',
    TYPE_NESTED: 'NestedEntity',

    getType: function (metaRecord) {
        var me = this;

        switch (true) {
            case me.isLookup(metaRecord):
                return me.TYPE_LOOKUP;
            case me.isEntity(metaRecord):
                return me.TYPE_ENTITY;
            case me.isNested(metaRecord):
                return me.TYPE_NESTED;
            default:
                throw new Error('Unknown entity');
        }
    },

    /**
     * Получить название типа сущности
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity|Unidata.model.entity.NestedEntity} Метамодель
     * @returns {String}
     */
    getTypeDisplayName: function (metaRecord) {
        var type,
            displayNames;

        displayNames = {
            LookupEntity: Unidata.i18n.t('glossary:lookupEntity'),
            Entity: Unidata.i18n.t('glossary:entity'),
            NestedEntity: Unidata.i18n.t('glossary:nestedEntity').toLowerCase()
        };

        type = this.getType(metaRecord);

        return displayNames[type];
    },

    /**
     * Получить короткое название типа сущности
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity|Unidata.model.entity.NestedEntity} Метамодель
     * @returns {String}
     */
    getTypeShortDisplayName: function (metaRecord) {
        var type,
            displayNames;

        displayNames = {
            LookupEntity: Unidata.i18n.t('util>lookupEntity'),
            Entity: Unidata.i18n.t('glossary:entity'),
            NestedEntity: Unidata.i18n.t('glossary:nestedEntityAbbr').toLowerCase()
        };

        type = this.getType(metaRecord);

        return displayNames[type];
    },

    /**
     * Возвращает true если metaRecord это реестр, иначе false
     *
     * @param metaRecord - метамодель: metaRecord или metaNested
     * @returns {boolean}
     */
    isEntity: function (metaRecord) {
        return metaRecord instanceof Unidata.model.entity.Entity;
    },

    /**
     * Возвращает true если metaRecord это справочник, иначе false
     *
     * @param metaRecord - метамодель: metaRecord или metaNested
     * @returns {boolean}
     */
    isLookup: function (metaRecord) {
        return metaRecord instanceof Unidata.model.entity.LookupEntity;
    },

    /**
     * Возвращает true если metaRecord это nested элемент (вложенный), иначе false
     *
     * @param metaRecord - метамодель: metaRecord или metaNested
     * @returns {boolean}
     */
    isNested: function (metaRecord) {
        return metaRecord instanceof Unidata.model.entity.NestedEntity;
    },

    /**
     * Локально сортирует store по возврастанию / убыванию
     *
     * @param store     - сортируемый стор
     * @param direction - направление сортировки: 'ASC' или 'DESC'
     */
    localSortAttributeByOrder: function (store, direction) {
        var remoteSortPrev;

        if (direction !== 'DESC') {
            direction = 'ASC';
        }

        remoteSortPrev = store.getRemoteSort();
        store.setRemoteSort(false);

        store.sort('order', direction);

        store.setRemoteSort(remoteSortPrev);
    },

    /**
     * Локально сортирует complexAttributes переданной метамодели по возврастанию / убыванию значения поля order
     *
     * @param metaRecord - метамодель: metaRecord или metaNested
     * @param direction  - направление сортировки: 'ASC' или 'DESC'
     */
    localSortComplexAttributeByOder: function (metaRecord, direction) {
        var complexAttributes;

        // у модели в принципе нет комплексных атрибутов
        if (!this.hasComplexAttribute(metaRecord)) {
            return attributes;
        }

        complexAttributes = metaRecord.complexAttributes();

        if (complexAttributes) {
            this.localSortAttributeByOrder(complexAttributes, direction);
        }
    },

    /**
     * Локально сортирует simpleAttributes переданной метамодели по возврастанию / убыванию значения поля order
     *
     * @param metaRecord - метамодель: metaRecord или metaNested
     * @param direction  - направление сортировки: 'ASC' или 'DESC'
     */
    localSortSimpleAttributeByOder: function (metaRecord, direction) {
        var simpleAttributes;

        simpleAttributes = metaRecord.simpleAttributes();

        if (simpleAttributes) {
            this.localSortAttributeByOrder(simpleAttributes, direction);
        }
    },

    /**
     * Локально сортирует arrayAttributes переданной метамодели по возврастанию / убыванию значения поля order
     *
     * @param metaRecord - метамодель: metaRecord или metaNested
     * @param direction  - направление сортировки: 'ASC' или 'DESC'
     */
    localSortArrayAttributeByOder: function (metaRecord, direction) {
        var arrayAttributes;

        arrayAttributes = metaRecord.arrayAttributes();

        if (arrayAttributes) {
            this.localSortAttributeByOrder(arrayAttributes, direction);
        }
    },

    /**
     * Возвращает массив путей атрибутов участвующих в правиле качества с именем dqRule
     *
     * @param metaRecord - модель записи
     * @param dqName     - имя правила качества
     * @returns {*}
     */
    getDqAttributePathsByName: function (metaRecord, dqName) {
        var attributePaths = [];

        metaRecord.dataQualityRules().each(function (dqRule) {
            var inputs;

            if (dqRule.get('name') === dqName) {
                inputs = dqRule.inputs();

                inputs.each(function (input) {
                    var attributeName = input.get('attributeName');

                    if (attributeName !== null) {
                        attributePaths.push(attributeName);
                    }
                });
            }
        });

        return Ext.Array.unique(attributePaths);
    },

    /**
     * Возвращает массив путей атрибутов участвующих в правилах качества с именем dqNames
     *
     * @param metaRecord - модель записи
     * @param dqNames    - массив имен правил качества
     * @returns {*}
     */
    getDqAttributePathsByNames: function (metaRecord, dqNames) {
        var me             = this,
            attributePaths = [];

        Ext.Array.each(dqNames, function (dqName) {
            var dqAttributePaths;

            dqAttributePaths = me.getDqAttributePathsByName(metaRecord, dqName);

            attributePaths = Ext.Array.merge(attributePaths, dqAttributePaths);
        });

        return Ext.Array.unique(attributePaths);
    },

    /**
     * Возвращает массив путей атрибутов которые участвуют в правилах качества
     *
     * @param metaRecord - модель записи
     * @returns {*}
     */
    getAllDqAttributePaths: function (metaRecord) {
        var dqNames = [];

        metaRecord.dataQualityRules().each(function (dqRule) {
            dpNames.push(dqRule.get('name'));
        });

        return this.getDqAttributePathsByNames(metaRecord, dqNames);
    },

    /**
     * Возвращает истину если у модели определены связи типа ссылка
     *
     * @param metaRecord - модель записи
     * @returns {boolean}
     */
    hasReferenceRelation: function (metaRecord) {
        var rels = [];

        if (metaRecord && this.isEntity(metaRecord)) {
            rels = metaRecord.relations().query('relType', 'References', false, false, true);
        }

        return rels.length > 0;
    },

    /**
     * Возвращает истину если у модели определены связи типа включение
     *
     * @param metaRecord - модель записи
     * @returns {boolean}
     */
    hasContainsRelation: function (metaRecord) {
        var rels = [];

        if (metaRecord && this.isEntity(metaRecord)) {
            rels = metaRecord.relations().query('relType', 'Contains');
        }

        return rels.length > 0;
    },

    /**
     * Возвращает истину если у модели определены связи типа многие-ко-многим
     *
     * @param metaRecord - модель записи
     * @returns {boolean}
     */
    hasM2mRelation: function (metaRecord) {
        var rels = [];

        if (metaRecord && this.isEntity(metaRecord)) {
            rels = metaRecord.relations().query('relType', 'ManyToMany');
        }

        return rels.length > 0;
    },

    /**
     * Возвращает истину если у модели могут быть определены комплексные атрибуты
     *
     * @param metaRecord
     * @returns {boolean}
     */
    hasComplexAttribute: function (metaRecord) {
        var hasComplex = false;

        if (metaRecord && this.isEntity(metaRecord) || this.isNested(metaRecord)) {
            hasComplex = true;
        }

        return hasComplex;
    },

    /**
     * Возвращает все простые атрибуты (включая вложенные простые)
     *
     * @param metaRecord
     * @returns {*|{displayName, editor, renderer}|Array}
     */
    getAllAttributes: function (metaRecord) {
        var me         = this,
            attributes = [],
            isLookup   = Unidata.util.MetaRecord.isLookup(metaRecord),
            codeAttribute,
            aliasCodeAttributes,
            simpleAttributes,
            complexAttributes,
            arrayAttributes;

        simpleAttributes  = metaRecord.simpleAttributes().getRange();

        // для справочника учитываем кодовые атрибуты
        if (isLookup) {
            codeAttribute       = metaRecord.getCodeAttribute();
            aliasCodeAttributes = metaRecord.aliasCodeAttributes().getRange();
        }

        arrayAttributes  = metaRecord.arrayAttributes().getRange();

        attributes = Ext.Array.merge(attributes, codeAttribute, aliasCodeAttributes, simpleAttributes, arrayAttributes);

        if (this.hasComplexAttribute(metaRecord)) {
            complexAttributes = metaRecord.complexAttributes();

            // если есть комплексные то вложенные в них
            complexAttributes.each(function (complexAttribute) {
                var metaNested            = complexAttribute.getNestedEntity(),
                    nestedSimpleAttribute = me.getAllAttributes(metaNested);

                attributes = Ext.Array.merge(attributes, nestedSimpleAttribute);
            });
        }

        // атрибут должен быть определен
        attributes = Ext.Array.filter(attributes, function (item) {
            return Boolean(item);
        });

        return Ext.Array.unique(attributes);
    },

    /**
     * Возвращает все комплексные атрибуты (включая вложенные комплексные)
     *
     * @param metaRecord
     * @returns {*|{displayName, editor, renderer}|Array}
     */
    getAllComplexAttributes: function (metaRecord) {
        var me         = this,
            attributes = [],
            complexAttributes;

        // у модели в принципе нет комплексных атрибутов
        if (!this.hasComplexAttribute(metaRecord)) {
            return attributes;
        }

        complexAttributes = metaRecord.complexAttributes();

        // если есть комплексные то вложенные в них
        complexAttributes.each(function (complexAttribute) {
            var metaNested            = complexAttribute.getNestedEntity(),
                nestedSimpleAttribute = me.getAllComplexAttributes(metaNested);

            attributes.push(complexAttribute);

            attributes = Ext.Array.merge(attributes, nestedSimpleAttribute);
        });

        return Ext.Array.unique(attributes);
    },

    /**
     * Возвращает количество скрытых атрибутов в метамодели (комплексные + простые)
     *
     * @param metaRecord - модель записи
     * @returns {boolean}
     */
    getHiddenAttributeCount: function (metaRecord) {
        var attributes        = this.getAllAttributes(metaRecord),
            complexAttributes = this.getAllComplexAttributes(metaRecord),
            count             = 0;

        attributes = Ext.Array.merge(attributes, complexAttributes);
        attributes = Ext.Array.unique(attributes);

        // обходим все атрибуты в поисках скрытых
        Ext.Array.each(attributes, function (item) {
            if (Unidata.util.MetaAttribute.isHidden(item)) {
                count++;
            }
        });

        return count;
    },

    /**
     * Возвращает истину если у модели определены скрытые атрибуты
     *
     * @param metaRecord - модель записи
     * @returns {boolean}
     */
    hasHiddenAttribute: function (metaRecord) {
        var count = this.getHiddenAttributeCount(metaRecord);

        return count > 0;
    },

    isEqual: function (metaRecord1, metaRecord2) {
        return metaRecord1 && metaRecord2 &&
                metaRecord1.get('name') === metaRecord2.get('name') &&
                metaRecord1.get('type') === metaRecord2.get('type');
    },

    /**
     * Почистить componentState для отсутствующих metaRecord
     *
     * @param entities {Unidata.model.entity.Entity[]}
     * @param lookupEntities {Unidata.model.entity.LookupEntity[]}
     * @returns {null}
     */
    cleanComponentState: function (entities, lookupEntities) {
        var ComponentState = Unidata.module.ComponentState,
            stateEntityNames,
            entityNames,
            allEntities;

        if (!Ext.isArray(entities) && !Ext.isArray(lookupEntities)) {
            return null;
        }

        allEntities        = entities.concat(lookupEntities);
        entityNames        = Ext.Array.map(allEntities, function (entity) {
            return entity.get('name');
        });

        stateEntityNames = ComponentState.pluckFirstLevelKeys();

        Ext.Array.each(stateEntityNames, function (stateEntityName) {
            if (!Ext.Array.contains(entityNames, stateEntityName)) {
                console.log(Unidata.i18n.t('util>removedComponentState') + stateEntityName);
                ComponentState.removeNestedState([stateEntityName]);
            }
        });
    }
});

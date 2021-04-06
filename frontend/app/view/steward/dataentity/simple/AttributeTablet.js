/**
 * Класс реализует представление группы простых атрибутов
 *
 * @author Ivan Marshalkin
 * @date 2016-02-21
 */

Ext.define('Unidata.view.steward.dataentity.simple.AttributeTablet', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.steward.dataentity.attribute.SimpleAttribute',
        'Unidata.view.steward.dataentity.attribute.ArrayAttribute',
        'Unidata.view.steward.dataentity.attribute.CodeArrayAttribute'
    ],

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    cls: [
        'un-attribute-tablet',
        'un-attribute-tablet-rendering'
    ],

    config: {
        metaRecord: null,
        dataRecord: null,
        metaNested: null,
        dataNested: null,

        // attributeGroup - это объект со структурой
        //
        // {
        //     row: number,
        //     column: number,
        //     title: string,
        //     attributes: [attributes path]
        // }
        attributeGroup: null,

        readOnly: null,

        hiddenAttribute: null,
        preventMarkField: null,
        hideAttributeTitle: null,
        noWrapTitle: false
    },

    codeAttributeErrorText: Unidata.i18n.t('dataentity>codeAttributeMissing'),

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initComponent: function () {
        var me = this,
            metaNested = this.getMetaNested(),
            dataNested = this.getDataNested(),
            simpleAttributeContainers = [],
            arrayAttributeContainers = [],
            codeAttributeContainers = [],
            containers  = [],
            MetaRecordUtil = Unidata.util.MetaRecord;

        this.callParent(arguments);

        if (!metaNested) {
            throw 'metaNested is empty';
        }

        if (!dataNested) {
            throw 'dataNested is empty';
        }

        if (MetaRecordUtil.isLookup(metaNested)) {
            codeAttributeContainers = this.buildCodeAttributeContainers();
        }
        simpleAttributeContainers = this.buildSimpleAttributeContainers();

        // массивы могут быть в реестрах/связях/вложенных сущностях (но не на связях!)
        if (MetaRecordUtil.isEntity(metaNested) ||
            MetaRecordUtil.isLookup(metaNested) ||
            MetaRecordUtil.isNested(metaNested)) {
            arrayAttributeContainers = this.buildArrayAttributeContainers();
        }

        // теперь необходимо отсортировать контенеры
        containers = Ext.Array.merge(simpleAttributeContainers, arrayAttributeContainers, codeAttributeContainers);
        containers = this.sortContainers(containers);

        this.renderItemsByChunks(containers, 10, 0)
            .then(function () {
                // проверяем, что html-элемент таблетки существует
                // TODO: to Sergey Shishigin это аналог првоерки me.rendered?! (Ivan Marshalkin)
                if (!me.el && !me.protoEl) {
                    return;
                }
                me.removeCls('un-attribute-tablet-rendering');
            })
            .done();
    },

    /**
     * Рендерит айтемы по частям
     * @param items
     * @param size
     * @param position
     * @returns {Ext.promise.Promise}
     */
    renderItemsByChunks: function (items, size, position) {
        var me = this,
            chunk = items.slice(position, position + size),
            deferred = Ext.create('Ext.Deferred'),
            addedItems,
            promisedRenders = [];

        // если нечего рендерить - резолвим и выходим
        if (!chunk.length) {
            deferred.resolve();

            return deferred.promise;
        }

        // добавляем элементы
        addedItems = me.add(chunk);

        // всё, что добавили - обещаем отрендерить
        Ext.Array.each(addedItems, function (item) {
            var deferredRender = Ext.create('Ext.Deferred');

            promisedRenders.push(deferredRender.promise);

            if (item.rendered) {
                deferredRender.resolve();
            } else {
                item.on('afterrender', function () {
                    deferredRender.resolve();
                });
            }
        });

        // когда отрендерили пачку, запускаем в работу следующую
        Ext.Deferred.all(promisedRenders).then(function () {
            Ext.defer(function () {
                me.renderItemsByChunks(items, size, position + size).then(function () {
                    deferred.resolve();
                });
            }, 100);
        });

        return deferred.promise;
    },

    /**
     * Сортирует контейнеры учитывая порядок атрибутов в метамодели или учитывая порядок атрибутов в группе
     *
     * @param containers
     * @returns {*}
     */
    sortContainers: function (containers) {
        var me = this,
            indexed = [],
            unindexed = [],
            sorted = [];

        // Сперва сортируем по порядку на мета
        containers = Ext.Array.sort(containers, function (a, b) {
            var metaAttributeA = a.getMetaAttribute(),
                metaAttributeB = b.getMetaAttribute(),
                orderA = metaAttributeA.get('order'),
                orderB = metaAttributeB.get('order');

            if (orderA < orderB) {
                return -1;
            } else if (orderA > orderB) {
                return 1;
            }

            return 0;
        });

        Ext.Array.each(containers, function (container) {
            var metaAttribute,
                index;

            metaAttribute = container.getMetaAttribute();

            index = me.indexOfAttributeInGroup(metaAttribute);

            if (index == -1) {
                unindexed.push(container);
            } else {
                // сохраняем порядок, который задан в группе
                // на всякий случай учитываем возможность пересечения индексов
                indexed.push({
                    index: index,
                    container: container
                });
            }
        });

        // сортируем контейнеры
        indexed = Ext.Array.sort(indexed, function (a, b) {
            var result = 0;

            if (a.index < b.index) {
                result = -1;
            } else if (a.index > b.index) {
                result = 1;
            }

            return result;
        });

        indexed = Ext.Array.pluck(indexed, 'container');

        sorted = Ext.Array.merge(indexed, unindexed);

        return sorted;
    },

    /**
     * Возвращает массив компонентов содержащих элементы для атрибутов
     *
     * @param metaAttributes {Array}
     * @param buildFn {Function}
     * @returns {Array}
     */
    buildAttributeContainers: function (metaAttributes, buildFn) {
        var me = this,
            containers = [];

        Ext.Array.forEach(metaAttributes, function (metaAttribute) {
            var container;

            // создаем контейнер только если атрибут входит в отображаемую группу
            if (me.isAttributeInGroup(metaAttribute)) {
                container = buildFn.apply(me, [metaAttribute]);

                containers.push(container);
            }
        });

        return containers;
    },

    /**
     * Возвращает массив компонентов содержащих элементы для простых атрибутов
     *
     * @returns {Array}
     */
    buildSimpleAttributeContainers: function () {
        var metaNested = this.getMetaNested(),
            containers = [],
            metaAttributes;

        metaAttributes = metaNested.simpleAttributes().getRange();

        containers = this.buildAttributeContainers(metaAttributes, this.buildSimpleAttributeContainer);

        return containers;
    },

    /**
     * Возвращает массив компонентов содержащих элементы для кодовых атрибутов
     *
     * @returns {Array}
     */
    buildCodeAttributeContainers: function () {
        var metaNested = this.getMetaNested(),
            containers = [],
            codeAttribute,
            aliasCodeAttributes,
            metaAttributes = [];

        if (!Unidata.util.MetaRecord.isLookup(metaNested)) {
            return [];
        }

        codeAttribute = metaNested.getCodeAttribute();
        aliasCodeAttributes = metaNested.aliasCodeAttributes();
        metaAttributes.push(codeAttribute);
        metaAttributes = Ext.Array.merge(metaAttributes, aliasCodeAttributes.getRange());

        containers = this.buildAttributeContainers(metaAttributes, this.buildCodeAttributeContainer);

        return containers;
    },

    /**
     * Возвращает массив компонентов содержащих элементы для атрибутов-масивов
     *
     * @returns {Array}
     */
    buildArrayAttributeContainers: function () {
        var metaNested = this.getMetaNested(),
            containers = [],
            metaAttributes;

        metaAttributes = metaNested.arrayAttributes().getRange();

        containers = this.buildAttributeContainers(metaAttributes, this.buildArrayAttributeContainer);

        return containers;
    },

    /**
     * Возвращает компонент содержащий элементы для простых атрибутов
     *
     * @param metaAttribute
     * @returns {Unidata.view.steward.dataentity.attribute.SimpleAttribute|*}
     */
    buildSimpleAttributeContainer: function (metaAttribute) {
        var containerClassName = 'Unidata.view.steward.dataentity.attribute.SimpleAttribute',
            metaRecord       = this.getMetaRecord(),
            dataNested       = this.getDataNested(),
            attributeName    = metaAttribute.get('name'),
            measurementValueId = null,
            measurementUnitId = null,
            typeCategory = metaAttribute.get('typeCategory'),
            attributeType,
            dataAttribute,
            attributePath,
            container;

        attributeType = 'String';

        if (typeCategory === 'simpleDataType') {
            attributeType = metaAttribute.get('typeValue');
        } else if (typeCategory === 'lookupEntityType') {
            attributeType = metaAttribute.get('lookupEntityCodeAttributeType');
        }

        attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);
        dataAttribute = Unidata.util.UPathData.findFirstAttributeByPath(dataNested, attributeName);

        if (!dataAttribute) {
            if (metaAttribute.get('typeValue') === 'Number' && metaAttribute.get('valueId') !== null) {
                measurementValueId = metaAttribute.get('valueId');
                measurementUnitId = metaAttribute.get('defaultUnitId');
            }

            dataAttribute = Ext.create('Unidata.model.data.SimpleAttribute', {
                name: attributeName,
                value: null,
                type: attributeType,
                valueId: measurementValueId,
                unitId: measurementUnitId
            });

            dataNested.simpleAttributes().add(dataAttribute);
        }

        container = this.buildAttributeContainer(containerClassName,  metaAttribute, dataAttribute, attributePath);

        return container;
    },

    /**
     * Возвращает компонент содержащий элементы для кодовых атрибутов
     *
     * @param metaAttribute
     * @returns {Unidata.view.steward.dataentity.attribute.CodeAttribute|Unidata.view.steward.dataentity.attribute.AliasCodeAttribute}
     */
    buildCodeAttributeContainer: function (metaAttribute) {
        var metaRecord       = this.getMetaRecord(),
            dataNested       = this.getDataNested(),
            attributeName    = metaAttribute.get('name'),
            attributeType,
            dataAttribute,
            attributePath,
            container,
            containerClassName;

        if (metaAttribute.get('typeCategory') !== 'simpleDataType') {
            throw new Error(Unidata.i18n.t('dataentity:codeAttributeCanOnlySimpleDataType'));
        }

        attributeType = metaAttribute.get('typeValue');

        attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);
        dataAttribute = Unidata.util.UPathData.findFirstAttributeByPath(dataNested, attributeName);

        if (dataAttribute && !(dataAttribute instanceof Unidata.model.data.CodeAttribute)) {
            Unidata.showError(Unidata.i18n.t('dataentity:displayRecordError') + ' ' + this.codeAttributeErrorText);
            throw new Error(this.codeAttributeErrorText);
        }

        if (!dataAttribute) {
            // create dataAttribute if not exists
            dataAttribute = Ext.create('Unidata.model.data.CodeAttribute', {
                name: attributeName,
                value: null,
                type: attributeType
            });

            dataNested.codeAttributes().add(dataAttribute);
        }

        if (!dataAttribute.isSupplementary()) {
            containerClassName = 'Unidata.view.steward.dataentity.attribute.SimpleAttribute';
        } else {
            containerClassName = 'Unidata.view.steward.dataentity.attribute.CodeArrayAttribute';
        }

        container = this.buildAttributeContainer(containerClassName,  metaAttribute, dataAttribute, attributePath);

        return container;
    },

    /**
     * Возвращает компонент содержащий элементы для атрибутов-масивов
     *
     * @param metaAttribute
     * @returns
     */
    buildArrayAttributeContainer: function (metaAttribute) {
        var containerClassName = 'Unidata.view.steward.dataentity.attribute.ArrayAttribute',
            metaRecord       = this.getMetaRecord(),
            dataNested       = this.getDataNested(),
            attributeName    = metaAttribute.get('name'),
            attributeType    = metaAttribute.get('arrayDataType'),
            typeCategory    = metaAttribute.get('typeCategory'),
            dataAttribute,
            attributePath,
            container;

        attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);
        dataAttribute = Unidata.util.UPathData.findFirstAttributeByPath(dataNested, attributeName);

        if (typeCategory === 'lookupEntityType') {
            attributeType = metaAttribute.get('lookupEntityCodeAttributeType');
        } else {
            attributeType = metaAttribute.get('arrayDataType');
        }

        if (!dataAttribute) {
            dataAttribute = Ext.create('Unidata.model.data.ArrayAttribute', {
                name: attributeName,
                value: null,
                type: attributeType
            });

            dataNested.arrayAttributes().add(dataAttribute);
        }

        container = this.buildAttributeContainer(containerClassName, metaAttribute, dataAttribute, attributePath);

        return container;
    },

    /**
     * Возвращает компонент содержащий элементы для атрибутов
     *
     * @param containerClassName {String} Имя класса контейнера атрибута
     * @param metaAttribute {Unidata.model.attribute.SimpleAttribute|Unidata.model.attribute.CodeAttribute|Unidata.model.attribute.ArrayAttribute}
     * @param dataAttribute {Unidata.model.data.SimpleAttribute|Unidata.model.data.CodeAttribute|Unidata.model.data.ArrayAttribute}
     * @param attributePath {String}
     * @returns {Unidata.view.steward.dataentity.attribute.SimpleAttribute|Unidata.view.steward.dataentity.attribute.ArrayAttribute}
     */
    buildAttributeContainer: function (containerClassName, metaAttribute, dataAttribute, attributePath) {
        var metaRecord       = this.getMetaRecord(),
            dataRecord       = this.getDataRecord(),
            readOnly         = this.getReadOnly(),
            hiddenContainer  = false,
            preventMarkField = this.getPreventMarkField(),
            hideAttributeTitle = this.getHideAttributeTitle(),
            noWrapTitle       = this.getNoWrapTitle(),
            me = this,
            container,
            cfg;

        // атрибут возможно скрыт
        if (metaAttribute.get('hidden')) {
            hiddenContainer = !this.getHiddenAttribute();
        }

        cfg = {
            metaRecord: metaRecord,
            dataRecord: dataRecord,
            metaAttribute: metaAttribute,
            dataAttribute: dataAttribute,
            attributePath: attributePath,
            readOnly: readOnly,
            hidden: hiddenContainer,
            preventMarkField: preventMarkField,
            noWrapTitle: noWrapTitle
        };

        if (hideAttributeTitle !== null) {
            cfg = Ext.apply(cfg, {
                hideAttributeTitle: hideAttributeTitle
            });
        }

        container = Ext.create(containerClassName, cfg);
        this.relayEvents(container, ['layoutchanged']);

        return container;
    },

    /**
     * Возвращает true если атрибут входит в группу для отображения
     * Если группы не заданы то считаем что входит
     *
     * @param metaAttribute
     * @returns {boolean}
     */
    isAttributeInGroup: function (metaAttribute) {
        var metaRecord     = this.getMetaRecord(),
            attributeGroup = this.getAttributeGroup(),
            inGroup        = true,
            attributePath;

        attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);

        // если группа задана и атрибут не входит в нее => false
        if (Ext.isObject(attributeGroup) && Ext.isArray(attributeGroup.attributes)) {
            if (!Ext.Array.contains(attributeGroup.attributes, attributePath)) {
                inGroup = false;
            }
        }

        return inGroup;
    },

    indexOfAttributeInGroup: function (metaAttribute) {
        var metaRecord     = this.getMetaRecord(),
            attributeGroup = this.getAttributeGroup(),
            index          = -1,
            attributePath;

        attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);

        if (Ext.isObject(attributeGroup) && Ext.isArray(attributeGroup.attributes)) {
            if (Ext.Array.contains(attributeGroup.attributes, attributePath)) {
                index = attributeGroup.attributes.indexOf(attributePath);
            }
        }

        return index;
    },

    updateReadOnly: function (readOnly) {
        var containers = this.getSimpleAttributeContainers();

        Ext.Array.each(containers, function (container) {
            container.setReadOnly(readOnly);
        });
    },

    /**
     * Включает / выключает режим подсвечивания ошибок на ошибочных полях
     *
     * @param value
     */
    updatePreventMarkField: function (value) {
        var containers = this.getSimpleAttributeContainers();

        Ext.Array.each(containers, function (container) {
            container.setPreventMarkField(value);
        });
    },

    updateHiddenAttribute: function (hiddenAttribute) {
        var containers = this.getSimpleAttributeContainers();

        Ext.Array.each(containers, function (container) {
            var metaAttribute = container.getMetaAttribute(),
                hidden        = false;

            if (!hiddenAttribute && metaAttribute.get('hidden')) {
                hidden = true;
            }

            container.setHidden(hidden);
        });
    },

    getSimpleAttributeContainerByPath: function (attributePath) {
        var result,
            containers = this.getSimpleAttributeContainers();

        result = Ext.Array.find(containers, function (container) {
            return container.getAttributePath() === attributePath;
        });

        return result;
    },

    getSimpleAttributeContainerByAttributeName: function (attributeName) {
        var result,
            containers = this.getSimpleAttributeContainers();

        result = Ext.Array.find(containers, function (container) {
            return container.getMetaAttributeName() === attributeName;
        });

        return result;
    },

    getSimpleAttributeContainers: function () {
        var containers = [],
            items      = this.items;

        if (items && items.isMixedCollection) {
            containers = items.getRange();
        }

        return containers;
    },

    buildTabletKey: function () {
        var attributeGroup = this.attributeGroup,
            groupType = attributeGroup.groupType,
            key = null;

        if (groupType === 'METAMODELUNGROUP') {
            key = 'ungroup';
        } else if (groupType === 'METAMODEL')  {
            key = String(attributeGroup.row) + '-' + String(attributeGroup.column);
        }

        return key;
    },

    isAllContainersInTabletHidden: function () {
        var items = this.items.getRange(),
            everyHidden;

        // если элементов вообще нет (массив пустой), то возвращается everyHidden=true
        everyHidden = Ext.Array.every(items, function (item) {
            return item.hidden;
        });

        return everyHidden;
    }
});

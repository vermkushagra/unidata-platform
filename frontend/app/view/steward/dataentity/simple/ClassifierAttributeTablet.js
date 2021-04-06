/**
 * Класс реализует представление группы простых атрибутов пришедших с классификаторов
 *
 * @author Ivan Marshalkin
 * @date 2016-08-16
 */

Ext.define('Unidata.view.steward.dataentity.simple.ClassifierAttributeTablet', {
    extend: 'Ext.container.Container',
    //extend: 'Ext.panel.Panel',

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer',
        nodata: 'Unidata.mixin.NoDataDisplayable'
    },

    ui: 'card',
    cls: 'un-dataentity-classifier',
    noDataText: Unidata.i18n.t('dataentity>recordNotClassified'),

    config: {
        metaRecord: null,
        dataRecord: null,
        classifier: null,
        metaClassifierNode: null,
        dataClassifierNode: null,
        readOnly: null,
        hiddenAttribute: null,
        preventMarkField: null
    },

    layout: {
        type: 'vbox',
        align: 'left'
    },

    initComponent: function () {
        var me = this,
            readOnly = this.getReadOnly(),
            dataClassifierNode = this.getDataClassifierNode(),
            containers;

        this.callParent(arguments);

        if (!readOnly) {
            this.initClassifierTree();
        }

        containers = this.buildSimpleAttributeContainers();

        if (!dataClassifierNode) {
            this.showNoData(this.noDataText);
        }

        this.renderItemsByChunks(containers, 10, 0)
            .then(function () {
                me.removeCls('un-attribute-tablet-rendering');
            })
            .done();

        // this.add(containers);
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
     * Инициализирует начальное состояние дерева
     */
    initClassifierTree: function () {
        var me = this,
            readOnly = this.getReadOnly(),
            classifier = this.getClassifier(),
            metaRecord = this.getMetaRecord(),
            dataRecord = this.getDataRecord(),
            metaClassifierNode = this.getMetaClassifierNode(),
            classifierName = classifier.get('classifierName'),
            classifierNodeId = metaClassifierNode ? metaClassifierNode.getId() : null;

        this.add({
            xtype: 'dataclassifier.classifieritempanel',
            readOnly: readOnly,
            collapsed: true,
            metaRecord: metaRecord,
            dataRecord: dataRecord,
            classifier: classifier,
            classifierNodeId: classifierNodeId,
            width: '50%'
        });

    },

    /**
     * Возвращает компонент содержащий элементы для простых атрибутов
     *
     * @param metaClassifierNodeAttr
     * @returns {Unidata.view.steward.dataentity.attribute.SimpleAttribute|*}
     */
    buildSimpleAttributeContainer: function (metaClassifierNodeAttr) {
        var metaClassifierNode = this.getMetaClassifierNode(),
            dataClassifierNode,
            metaRecord         = this.getMetaRecord(),
            dataRecord         = this.getDataRecord(),
            attributeName      = metaClassifierNodeAttr.get('name'),
            classifierName     = metaClassifierNode.get('classifierName'),
            readOnly           = this.getReadOnly(),
            hiddenContainer    = false,
            preventMarkField   = this.getPreventMarkField(),
            measurementValueId = null,
            measurementUnitId  = null,
            ClassifierDataRecordUtil = Unidata.util.ClassifierDataRecord,
            attributeType,
            dataClassifierNodeAttr,
            attributePath,
            container;

        attributeType = 'String';

        if (metaClassifierNodeAttr.get('typeCategory') === 'simpleDataType') {
            attributeType = metaClassifierNodeAttr.get('typeValue');
        }

        dataClassifierNode = ClassifierDataRecordUtil.getFirstClassifierNodeByClassifierName(dataRecord, classifierName);

        if (!dataClassifierNode) {
            dataClassifierNode = Ext.create('Unidata.model.data.Classifiers', {
                classifierName: classifierName,
                classifierNodeId: metaClassifierNode.get('id')
            });

            dataRecord.classifiers().add(dataClassifierNode);
        }

        dataClassifierNodeAttr = ClassifierDataRecordUtil.getClassifierNodeAttributeByAttributeName(dataClassifierNode, attributeName); // jscs:ignore maximumLineLength

        if (!dataClassifierNodeAttr) {
            if (metaClassifierNodeAttr.get('typeValue') === 'Number' && metaClassifierNodeAttr.get('valueId') !== null) {
                measurementValueId = metaClassifierNodeAttr.get('valueId');
                measurementUnitId = metaClassifierNodeAttr.get('defaultUnitId');
            }

            dataClassifierNodeAttr = Ext.create('Unidata.model.data.SimpleAttribute', {
                name: attributeName,
                value: metaClassifierNodeAttr.get('value'),
                type: attributeType,
                valueId: measurementValueId,
                unitId: measurementUnitId
            });

            dataClassifierNode.simpleAttributes().add(dataClassifierNodeAttr);
        }

        if (this.attributeMustBeReadOnly(metaClassifierNodeAttr)) {
            readOnly = true;
        }

        // атрибут возможно скрыт
        if (metaClassifierNodeAttr.get('hidden')) {
            hiddenContainer = !this.getHiddenAttribute();
        }

        container = Ext.create('Unidata.view.steward.dataentity.attribute.SimpleAttribute', {
            metaRecord: metaRecord,
            dataRecord: dataRecord,
            metaAttribute: metaClassifierNodeAttr,
            dataAttribute: dataClassifierNodeAttr,
            attributePath: attributeName, // TODO: Ivan Marshalkin что тут должен быть за путь?
            readOnly: readOnly,
            hidden: hiddenContainer,
            preventMarkField: preventMarkField
        });

        return container;
    },

    /**
     * Возвращает массив компонентов содержащих элементы для простых атрибутов
     *
     * @returns {Array}
     */
    buildSimpleAttributeContainers: function () {
        var me                 = this,
            metaClassifierNode = this.getMetaClassifierNode(),
            containers         = [];

        if (!metaClassifierNode) {
            return containers;
        }

        metaClassifierNode.nodeAttrs().each(function (metaClassifierNodeAttr) {
            var container;

            container = me.buildSimpleAttributeContainer(metaClassifierNodeAttr);

            containers.push(container);
        });

        return containers;
    },

    /**
     * Возвращает истину если атрибут должен быть readonly т.к. (следствие настроек модели)
     *
     * @param metaClassifierNodeAttr
     * @returns {boolean}
     */
    attributeMustBeReadOnly: function (metaClassifierNodeAttr) {
        var readOnly = false;

        // если значение указано на модели то редактировать атрибут запрещено
        if (metaClassifierNodeAttr.get('value') !== null) {
            readOnly = true;
        }

        // если по модели атрибут readOnly то редактировать атрибут запрещено
        if (metaClassifierNodeAttr.get('readOnly')) {
            readOnly = true;
        }

        return readOnly;
    },

    updateReadOnly: function (readOnly) {
        var me         = this,
            containers = this.getSimpleAttributeContainers();

        Ext.Array.each(containers, function (container) {
            var attributeReadOnly = readOnly,
                metaAttribute;

            if (!container.getMetaAttribute) {
                return true;
            }

            metaAttribute = container.getMetaAttribute();

            if (me.attributeMustBeReadOnly(metaAttribute)) {
                attributeReadOnly = true;
            }

            container.setReadOnly(attributeReadOnly);
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

    /**
     * Получить массив простых атрибутов
     *
     * @returns {Unidata.view.steward.dataentity.attribute.AbstractAttribute[]}
     */
    getSimpleAttributeContainers: function () {
        var containers = [],
            items      = this.items;

        if (items && items.isMixedCollection) {
            containers = items.getRange();
        }

        containers = Ext.Array.filter(containers, function (container) {
            return container instanceof Unidata.view.steward.dataentity.attribute.AbstractAttribute;
        });

        return containers;
    },

    buildTabletKey: function () {
        var classifier = this.getClassifier(),
            key;

        key = classifier.get('name');

        return key;
    }
});

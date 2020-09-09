/**
 * Класс реализует представление группы простых атрибутов пришедших с классификаторов
 *
 * @author Ivan Marshalkin
 * @date 2016-08-16
 */

Ext.define('Unidata.view.steward.dataentity.simple.ClassifierAttributeTablet', {
    // extend: 'Ext.container.Container',
    extend: 'Ext.panel.Panel',

    alias: 'widget.steward.dataentity.simple.classifierattributetablet',

    controller: 'steward.dataentity.simple.classifierattributetablet',
    viewModel: {
        type: 'steward.dataentity.simple.classifierattributetablet'
    },

    requires: [
        'Unidata.view.steward.dataentity.simple.ClassifierAttributeTabletController'
    ],

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer',
        nodata: 'Unidata.mixin.NoDataDisplayable',
        headertooltip: 'Unidata.mixin.HeaderTooltipable',
        highlight: 'Unidata.mixin.DataHighlightable'
    },
    ui: 'un-card',
    cls: 'un-dataentity-classifier',

    titleCollapse: true,
    collapsible: true,
    animCollapse: false,

    // сочетание collapseFirst:true и header.titlePosition:1 позволяет отобразить иконку сворачивания в начале header панели
    collapseFirst: true,
    header: {
        titlePosition: 1
    },

    config: {
        classifierTreeConfig: null, // кастомный конфиг для дерева
        metaRecord: null,
        dataRecord: null,
        classifier: null,
        metaClassifierNode: null,
        dataClassifierNode: null,
        readOnly: null,
        hiddenAttribute: null,
        preventMarkField: null,
        headerTooltip: null,
        attributePath: null,
        qaId: null
    },

    methodMapper: [
        {
            method: 'remove'
        },
        {
            method: 'showClassifierNodeEditorWindow'
        },
        {
            method: 'buildBaseToolTip'
        },
        {
            method: 'useSelectedClassifierNode'
        },
        {
            method: 'updateMetaClassifierNode'
        },
        {
            method: 'createClassifierNode'
        }
    ],

    bind: {
        title: '{title}'
    },

    viewModelAccessors: ['classifier', 'metaClassifierNode', 'readOnly'],

    classifierItem: null,

    tools: [
        {
            xtype: 'un.fontbutton',     // TODO: replace
            handler: 'onSelectClassifierNodeButtonClick',
            iconCls: 'icon-pencil4',
            bind: {
                hidden: '{readOnly}'
            }
        },
        {
            xtype: 'un.fontbutton.delete',
            reference: 'removeClassifierNodeButton',
            handler: 'onRemoveClassifierNodeButtonClick',
            //shadow: false,
            //buttonSize: 'extrasmall',
            tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:classificationNode')}),
            bind: {
                hidden: '{readOnly}'
            }
        }
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    getPreviousClassifierNodeSelected: function () {
        var metaClassifierNode = this.getMetaClassifierNode();

        return metaClassifierNode ? [metaClassifierNode] : [];
    },

    setPreviousClassifierNodeSelected: function (selected) {
        if (Ext.isArray(selected) && selected.length > 0) {
            this.setMetaClassifierNode(selected[0]);
        } else {
            this.setMetaClassifierNode(null);
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        this.buildAndRenderAttributeContainers();
    },

    buildAndRenderAttributeContainers: function () {
        var me = this,
            metaRecord = this.getMetaRecord(),
            simpleAttributeContainers,
            arrayAttributeContainers = [],
            containers,
            MetaRecordUtil = Unidata.util.MetaRecord;

        this.items.removeAll();
        simpleAttributeContainers = this.buildSimpleAttributeContainers();

        // массивы могут быть в реестрах/связях/вложенных сущностях (но не на связях!)
        if (MetaRecordUtil.isEntity(metaRecord) ||
            MetaRecordUtil.isLookup(metaRecord) ||
            MetaRecordUtil.isNested(metaRecord)) {
            arrayAttributeContainers = this.buildArrayAttributeContainers();
        }

        // теперь необходимо отсортировать контенеры
        containers = Ext.Array.merge(simpleAttributeContainers, arrayAttributeContainers);
        containers = this.sortContainers(containers);

        if (!containers.length) {
            this.setCollapsed(true);
            containers.push(this.buildNoDataComponentCfg(Unidata.i18n.t('dataentity>noAttributes')));
        } else {
            this.setCollapsed(false);
        }

        this.renderItemsByChunks(containers, 10, 0)
            .then(function () {
                if (me.rendered) {
                    me.removeCls('un-attribute-tablet-rendering');
                }
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
     * Возвращает компонент содержащий элементы для простых атрибутов
     *
     * @param metaClassifierNodeAttr
     * @returns {Unidata.view.steward.dataentity.attribute.SimpleAttribute|*}
     */
    buildSimpleAttributeContainer: function (metaClassifierNodeAttr) {
        var dataClassifierNode = this.getDataClassifierNode(),
            metaRecord = this.getMetaRecord(),
            dataRecord = this.getDataRecord(),
            attributeName = metaClassifierNodeAttr.get('name'),
            readOnly = this.getReadOnly(),
            hiddenContainer = false,
            preventMarkField = this.getPreventMarkField(),
            measurementValueId = null,
            measurementUnitId = null,
            ClassifierDataRecordUtil = Unidata.util.ClassifierDataRecord,
            attributeType,
            dataClassifierNodeAttr,
            container;

        attributeType = 'String';

        if (metaClassifierNodeAttr.get('typeCategory') === 'simpleDataType') {
            attributeType = metaClassifierNodeAttr.get('typeValue');
        } else if (metaClassifierNodeAttr.get('typeCategory') === 'lookupEntityType') {
            attributeType = metaClassifierNodeAttr.get('lookupEntityCodeAttributeType');
        }

        dataClassifierNodeAttr = ClassifierDataRecordUtil.getClassifierNodeAttributeByAttributeName(dataClassifierNode, attributeName);

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
     * Возвращает компонент содержащий элементы для атрибутов-масивов
     *
     * @param metaClassifierNodeAttr
     * @returns {Unidata.view.steward.dataentity.attribute.SimpleAttribute|*}
     */
    buildArrayAttributeContainer: function (metaClassifierNodeAttr) {
        var metaClassifierNode = this.getMetaClassifierNode(),
            dataClassifierNode = this.getDataClassifierNode(),
            metaRecord = this.getMetaRecord(),
            dataRecord = this.getDataRecord(),
            attributeName = metaClassifierNodeAttr.get('name'),
            classifierName = metaClassifierNode.get('classifierName'),
            readOnly = this.getReadOnly(),
            hiddenContainer = false,
            preventMarkField = this.getPreventMarkField(),
            measurementValueId = null,
            measurementUnitId = null,
            ClassifierDataRecordUtil = Unidata.util.ClassifierDataRecord,
            attributeType,
            dataClassifierNodeAttr,
            container;

        attributeType = 'String';

        if (metaClassifierNodeAttr.get('typeCategory') === 'lookupEntityType') {
            attributeType = metaClassifierNodeAttr.get('lookupEntityCodeAttributeType');
        } else {
            attributeType = metaClassifierNodeAttr.get('arrayDataType');
        }

        dataClassifierNodeAttr = ClassifierDataRecordUtil.getClassifierNodeAttributeByAttributeName(dataClassifierNode, attributeName);

        if (!dataClassifierNodeAttr) {
            if (metaClassifierNodeAttr.get('typeValue') === 'Number' && metaClassifierNodeAttr.get('valueId') !== null) {
                measurementValueId = metaClassifierNodeAttr.get('valueId');
                measurementUnitId = metaClassifierNodeAttr.get('defaultUnitId');
            }

            dataClassifierNodeAttr = Ext.create('Unidata.model.data.ArrayAttribute', {
                name: attributeName,
                value: metaClassifierNodeAttr.get('value'),
                type: attributeType,
                valueId: measurementValueId,
                unitId: measurementUnitId
            });

            dataClassifierNode.arrayAttributes().add(dataClassifierNodeAttr);
        }

        if (this.attributeMustBeReadOnly(metaClassifierNodeAttr)) {
            readOnly = true;
        }

        // атрибут возможно скрыт
        if (metaClassifierNodeAttr.get('hidden')) {
            hiddenContainer = !this.getHiddenAttribute();
        }

        container = Ext.create('Unidata.view.steward.dataentity.attribute.ArrayAttribute', {
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
        var me = this,
            metaClassifierNode = this.getMetaClassifierNode(),
            containers = [];

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
     * Возвращает массив компонентов содержащих элементы для атрибутов-масивов
     *
     * @returns {Array}
     */
    buildArrayAttributeContainers: function () {
        var me = this,
            metaClassifierNode = this.getMetaClassifierNode(),
            containers = [];

        if (!metaClassifierNode) {
            return containers;
        }

        metaClassifierNode.nodeArrayAttrs().each(function (metaClassifierNodeAttr) {
            var container;

            container = me.buildArrayAttributeContainer(metaClassifierNodeAttr);

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
        var me = this,
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
            items = this.items;

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
    },

    /**
     * Сортирует контейнеры учитывая порядок атрибутов
     *
     * @param containers
     * @returns {*}
     */
    sortContainers: function (containers) {
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

        return containers;
    },

    buildHeaderTooltip: function () {
        var classifier = this.getClassifier(),
            metaClassifierNode = this.getMetaClassifierNode();

        return Unidata.util.Classifier.buildClassifierNodeTitle(classifier, metaClassifierNode);
    },

    /**
     * Рендеринг компонента
     */
    onRender: function () {
        var headerTooltip;

        this.callParent(arguments);

        headerTooltip = this.buildHeaderTooltip();
        this.setHeaderTooltip(headerTooltip);

        if (headerTooltip) {
            this.getHeader().on('render', function () {
                this.initTitleTooltip();
            }, this);
        }
    }
});

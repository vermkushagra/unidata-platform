/**
 * Список поисковых панелей классификаторов
 */
Ext.define('Unidata.view.component.search.query.classifier.ClassifierFilterList', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.search.query.classifier.ClassifierFilterItem'
    ],

    alias: 'widget.component.search.query.classifierfilterlist',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-search-filter',

    cls: 'un-classifier-filter-list',

    title: Unidata.i18n.t('glossary:classifiers'),

    collapsible: true,
    titleCollapse: true,
    collapsed: true,
    // disabled: true,

    config: {
        allowedClassifiers: null
    },

    scrollable: false,

    items: [],

    initComponent: function () {
        this.callParent(arguments);
        this.createClassifierFilterItem();
    },

    addClassifierFilterItem: function (item) {
        item = this.add(item);
        item.on('classifieritemnodeselect', this.onClassifierFilterNodeSelect, this);
        item.on('classifieritemnodereset', this.onClassifierFilterNodeReset, this);
        this.relayEvents(item, ['change']);
    },

    createClassifierFilterItem: function () {
        var item,
            allowedClassifiers = this.getAllowedClassifiers();

        item = Ext.create('Unidata.view.component.search.query.classifier.ClassifierFilterItem');
        item.setAllowedClassifiers(allowedClassifiers);

        this.addClassifierFilterItem(item);
    },

    removeClassifierFilterItem: function (item) {
        this.remove(item);
    },

    onClassifierFilterNodeSelect: function (item) {
        this.onClassifierNodeChanged(item, {
            action: 'SET'
        });
    },

    onClassifierFilterNodeReset: function (item) {
        this.onClassifierNodeChanged(item, {
            action: 'RESET'
        });
    },

    onClassifierNodeChanged: function (item, cfg) {
        var action = cfg.action,
            allowedEntities;

        if (!this.isClassifierFilterItem(item)) {
            return;
        }

        this.calcClassifierFilterItemsState(action, item);
        allowedEntities = this.calcAllowedEntities();
        this.fireEvent('allowedentitieschanged', allowedEntities);
    },

    isClassifierFilterItem: function (item) {
        return item instanceof Unidata.view.component.search.query.classifier.ClassifierFilterItem;
    },

    /**
     * Пересчет состояния списка (удаляем/добавляем)
     *
     * @param action
     * @param item
     */
    calcClassifierFilterItemsState: function (action, item) {
        var items = this.items,
            index,
            count = items.getCount();

        if (!action || !item) {
            return;
        }

        index = items.indexOf(item);

        if (!Ext.isNumber(index)) {
            return;
        }

        switch (action) {
            case 'SET':
                // node set (select) for the last ClassifierFilterItem
                if (index === count - 1) {
                    this.createClassifierFilterItem();
                }
                break;
            case 'RESET':
                // node reset
                if (index < count - 1 && count > 1) {
                    this.removeClassifierFilterItem(item);
                }
                break;
            default:
                throw new Error('Unknown action: ' + action);
        }
    },

    updateAllowedClassifiers: function (allowedClassifiers) {
        var items = this.items;

        items.each(function (item) {
            if (!this.isClassifierFilterItem(item)) {
                return;
            }

            item.setAllowedClassifiers(allowedClassifiers);
        }, this);
    },

    resetList: function () {
        var items = this.items,
            length = items.length,
            item,
            i;

        for (i = 0; i < length - 1; i++) {
            // всегда удаляем первый item
            item = items.getAt(0);
            this.removeClassifierFilterItem(item);
        }

        item = items.getAt(0);
        item.setAllowedClassifiers(null);
    },

    calcAllowedEntities: function () {
        var items = this.items.getRange(),
            allowedEntitiesList,
            allowedEntities;

        items = Ext.Array.filter(items, function (item) {
            return item !== null && item.getAllowedEntities() !== null;
        });

        allowedEntitiesList = Ext.Array.map(items, this.getItemAllowedEntities, this);
        allowedEntities = Ext.Array.intersect.apply(this, allowedEntitiesList);

        return allowedEntities;
    },

    getItemAllowedEntities: function (item) {
        return item.getAllowedEntities();
    },

    getClassifierNodes: function () {
        var items = this.items.getRange(),
            classifierNodes;

        classifierNodes = Ext.Array.map(items, function (item) {
            return item.getClassifierNode();
        });

        classifierNodes = Ext.Array.filter(classifierNodes, function (classifierNode) {
            return classifierNode !== null;
        });

        return classifierNodes;
    },

    setDisabled: function (value) {
        var items = this.items.getRange();

        Ext.Array.each(items, function (item) {
            item.setDisabled(value);
        });
    },

    isEmptyFilter: function () {
        var items = this.items.getRange(),
            isEmptyFilter;

        isEmptyFilter = Ext.Array.every(items, function (item) {
            return item.isEmptyFilter();
        });

        return isEmptyFilter;
    },

    getFilter: function (classifierNode) {
        var items = this.items.getRange(),
            filters = [];

        Ext.Array.each(items, function (item) {
            var classifierFilterPanel = item.lookupReference('classifierFilterPanel');

            if (classifierFilterPanel && classifierNode === classifierFilterPanel.getClassifierNode()) {
                filters = filters.concat(classifierFilterPanel.getFilter());
            }
        });

        return filters;
    },

    excludeField: function (attributePath) {
        var items = this.items.getRange();

        Ext.Array.each(items, function (item) {
            item.excludeField(attributePath);
        });
    }
});

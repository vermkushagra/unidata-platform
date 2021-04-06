/**
 * Таблица записей
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.relation.component.RecordGrid', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.component.search.query.relation.component.recordgrid',

    store: {
        // model stored in property records from Unidata.module.EtalonCluster
        model: 'Unidata.model.search.SearchHit',
        //pageSize: Unidata.Config.getCustomerCfg()['RELATION_SEARCH_ROWS'],
        proxy: {
            //enablePaging: true,
            type: 'memory'
        }
    },

    hideHeaders: true,
    cls: 'un-record-grid',

    selModel: {
        selType: 'checkboxmodel',
        checkOnly: true,
        pruneRemoved: false,
        injectCheckbox: 'last'
    },

    config: {
        metaRecord: null,
        toEntityDefaultDisplayAttributes: null,
        selectedCount: 0,
        firstRecordsCount: 5,
        totalRecords: 0,
        relation: null
    },

    listeners: {
        selectionchange: 'onSelectionChange',
        scope: 'this'
    },

    simpleSwitcher: null,
    referenceHolder: true,

    viewModelAccessors: ['selectedCount', 'firstRecordsCount', 'totalRecords'],

    viewModel: {
        data: {
            selectedCount: 0,
            firstRecordsCount: 0,
            totalRecords: 0
        },

        formulas: {
            deleteSearchHitsEnabled: {
                bind: {
                    selectedCount: '{selectedCount}'
                },
                get: function (getter) {
                    var selectedCount = getter.selectedCount;

                    return selectedCount > 0;
                }
            },
            simpleSwitcherVisible: {
                bind: {
                    firstRecordsCount: '{firstRecordsCount}',
                    totalRecords: '{totalRecords}'
                },
                get: function (getter) {
                    var firstRecordsCount = getter.firstRecordsCount,
                        totalRecords = getter.totalRecords;

                    return totalRecords > firstRecordsCount;
                }
            },
            emptyListLabelVisible: {
                bind: {
                    simpleSwitcherVisible: '{simpleSwitcherVisible}',
                    totalRecords: '{totalRecords}'
                },
                get: function (getter) {
                    var simpleSwitcherVisible = getter.simpleSwitcherVisible,
                        totalRecords = getter.totalRecords,
                        visible;

                    visible = (totalRecords === 0) && !simpleSwitcherVisible;

                    return visible;
                }
            }
        }
    },

    initComponent: function () {
        this.dockedItems = this.buildDockedItems();
        this.createColumns();
        this.callParent(arguments);
        this.initReferences();
        this.initListeners();
    },

    initReferences: function () {
        this.simpleSwitcher = this.lookupReference('simpleSwitcher');
    },

    initListeners: function () {
        this.store.on('datachanged', this.onStoreDataChanged, this);
    },

    onStoreDataChanged: function () {
        var store = this.getStore(),
            count = store.getDataSource().getRange().length;

        this.setTotalRecords(count);
    },

    updateTotalRecords: function (count) {
        this.fireEvent('countchange', count);
    },

    buildDockedItems: function () {
        var dockedItems;

        dockedItems = [{
            xtype: 'toolbar',
            reference: 'toolbar',
            dock: 'bottom',
            padding: 10,
            items: [
                {
                    xtype: 'un.fontbutton.additem',
                    reference: 'addSearchHitsButton',
                    handler: this.onAddSearchHitsButtonClick.bind(this),
                    buttonSize: 'extrasmall',
                    shadow: false,
                    tooltip: Unidata.i18n.t('search>query.addLinkedRecord'),
                    bind: {
                        disabled: '{!addSearchHitsButtonEnabled}'
                    }
                },
                {
                    xtype: 'un.simpleswitcher',
                    flex: 1,
                    reference: 'simpleSwitcher',
                    style: {
                        textAlign: 'center'
                    },
                    stateCfgs: [
                        {
                            title: Unidata.i18n.t('search>query.showMore'),
                            handlerParams: {
                                handler: this.showAllRecords,
                                scope: this
                            }
                        },
                        {
                            title: Unidata.i18n.t('search>query.hide'),
                            handlerParams: {
                                handler: this.showOnlyFirstRecords,
                                scope: this
                            }
                        }
                    ],
                    bind: {
                        hidden: '{!simpleSwitcherVisible}'
                    }
                },
                {
                    xtype: 'label',
                    text: Unidata.i18n.t('search>query.recordsNotSelected'),
                    style: {
                        textAlign: 'center'
                    },
                    flex: 1,
                    cls: 'un-empty-list-label',
                    bind: {
                        hidden: '{!emptyListLabelVisible}'
                    }
                },
                {
                    xtype: 'un.fontbutton',
                    reference: 'deleteSearchHitsButton',
                    iconCls: 'icon-trash2',
                    handler: this.onDeleteSearchHitsButtonClick.bind(this),
                    tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:linkedRecords')}),
                    bind: {
                        disabled: '{!deleteSearchHitsEnabled}'
                    }
                }
            ]
        }, {

        }
        ];

        return dockedItems;
    },

    showAllRecords: function () {
        var store = this.getStore();

        store.clearFilter();
    },

    showOnlyFirstRecords: function () {
        var store = this.getStore();

        store.addFilter({
            filterFn: this.filterOnlyFirstRecords.bind(this)
        });
    },

    filterOnlyFirstRecords: function (searchHit) {
        var store = this.getStore(),
            firstRecordsCount = this.getFirstRecordsCount();

        return store.indexOf(searchHit) < firstRecordsCount;
    },

    createColumns: function () {
        this.columns = [{
            resizable: false,
            sortable: false,
            hideable: false,
            focusable: false,
            border: false,
            flex: 1,
            dataIndex: 'preview',
            renderer: this.renderColumn.bind(this)
        }];
    },

    getEtalonIds: function () {
        var SearchHitUtil = Unidata.util.SearchHit,
            store = this.getStore(),
            searchHits = store.getRange();

        return SearchHitUtil.pluckEtalonIds(searchHits);
    },

    isEtalonIdExists: function (etalonId) {
        var etalonIds = this.getEtalonIds(),
            index;

        index = Ext.Array.indexOf(etalonIds, etalonId);

        return index > -1;
    },

    /**
     * Отфильтровать searchHits, которые не присутствуют в списке
     *
     * @param searchHits
     * @returns {*}
     */
    filterSearchHitsNotExists: function (searchHits) {
        searchHits = Ext.Array.filter(searchHits, function (searchHit) {
            return !this.isEtalonIdExists(searchHit.get('id'));
        }, this);

        return searchHits;
    },

    /**
     * Добавить searchHit в список
     *
     * @param searchHit
     */
    addSearchHit: function (searchHit) {
        var SimpleSwitcher = Unidata.view.component.SimpleSwitcher,
            store = this.getStore(),
            simpleSwitcher = this.simpleSwitcher,
            currentState   = simpleSwitcher.getCurrentState();

        if (this.isEtalonIdExists(searchHit)) {
            return;
        }

        store.clearFilter();
        store.add(searchHit);

        if (currentState === SimpleSwitcher.states.STATE_ONE) {
            this.showOnlyFirstRecords();
        }
    },

    /**
     * Удалить searchHit из списка
     *
     * @param searchHit
     */
    removeSearchHit: function (searchHit) {
        var SimpleSwitcher = Unidata.view.component.SimpleSwitcher,
            store = this.getStore(),
            simpleSwitcher = this.simpleSwitcher,
            currentState   = simpleSwitcher.getCurrentState();

        store.clearFilter();
        store.remove(searchHit);
        store.add(searchHit);

        if (currentState === SimpleSwitcher.states.STATE_ONE) {
            this.showOnlyFirstRecords();
        }
    },

    /**
     * Добавить searchHits в список
     *
     * @param searchHits
     */
    addSearchHits: function (searchHits) {
        var SimpleSwitcher = Unidata.view.component.SimpleSwitcher,
            store = this.getStore(),
            simpleSwitcher = this.simpleSwitcher,
            currentState   = simpleSwitcher.getCurrentState();

        searchHits = this.filterSearchHitsNotExists(searchHits);

        store.clearFilter();
        store.add(searchHits);

        if (currentState === SimpleSwitcher.states.STATE_ONE) {
            this.showOnlyFirstRecords();
        }
    },

    /**
     * Удалить searchHits из списка
     *
     * @param searchHits
     */
    removeSearchHits: function (searchHits) {
        var SimpleSwitcher = Unidata.view.component.SimpleSwitcher,
            store = this.getStore(),
            simpleSwitcher = this.simpleSwitcher,
            currentState   = simpleSwitcher.getCurrentState();

        store.clearFilter();
        store.remove(searchHits);

        if (currentState === SimpleSwitcher.states.STATE_ONE) {
            this.showOnlyFirstRecords();
        }
    },

    /**
     * Найти searchHit по etalonId
     *
     * @param etalonId
     * @returns {*|Ext.data.Model}
     */
    findSearchHitByEtalonId: function (etalonId) {
        var store = this.getStore(),
            searchHit;

        searchHit = store.findRecord('id', etalonId, 0, false, false, true);

        return searchHit;
    },

    /**
     * Проверка searchHit на равенство
     *
     * @param a
     * @param b
     * @returns {boolean}
     */
    searchHitEqual: function (a, b) {
        var result = false;

        if (a === b) {
            result = true;
        } else if ((Boolean(a) && !Boolean(b)) || (!Boolean(a) && Boolean(b))) {
            result = false;
        } else {
            result = a.get('id') === b.get('id');
        }

        return result;
    },

    /**
     * Рендеринг полей в основной колонке выдачи
     *
     * @param value
     * @param metadata
     * @param record
     * @returns {string|*}
     */
    renderColumn: function (value, metadata, record) {
        var me = this,
            str,
            metaRecord = this.getMetaRecord(),
            template = '',
            preview = record.getAssociatedData()['preview'],
            relation = this.getRelation(),
            toEntityDefaultDisplayAttributes = relation.get('toEntityDefaultDisplayAttributes'),
            useAttributeNameForDisplay = relation.get('useAttributeNameForDisplay'),
            delimiter,
            mainItems,
            pathsDisplayable,
            items = [],
            parseFormats,
            metaAttribute;

        parseFormats = {
            Date: 'Y-m-d',
            Timestamp: 'Y-m-d\\TH:i:s',
            Time: '\\TH:i:s'
        };

        preview = preview.map(function (item) {
            var metaAttribute = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, item.field);

            item.order = 0;

            if (metaAttribute) {
                item.order = metaAttribute.get('order') || 0;
            }

            return item;
        });

        // сортируем отображаемые в порядке определения в модели
        preview = Ext.Array.sort(preview, function (a, b) {
            if (a.order < b.order) {
                return -1;
            } else if (a.order > b.order) {
                return 1;
            }

            return 0;
        });

        pathsDisplayable = Unidata.util.UPathMeta.buildAttributePaths(metaRecord, [{
            fn: Ext.bind(
                this.displayableAttributesFilter,
                this,
                [toEntityDefaultDisplayAttributes],
                true
            )
        }]);

        mainItems = Ext.Array.filter(preview, function (item) {
            return Ext.Array.contains(pathsDisplayable, item.field);
        }, this);

        mainItems.forEach(function (item) {
            var DataAttributeFormatter = Unidata.util.DataAttributeFormatter,
                displayName,
                formattedValue,
                formattedValues,
                fullValue;

            template        = '<p>{0}<b>{1}</b></p>';
            metaAttribute   = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, item.field);
            displayName     = metaAttribute.get('displayName');
            formattedValue  = DataAttributeFormatter.formatValueByAttribute(metaAttribute, item.value, parseFormats);
            formattedValues = DataAttributeFormatter.formatValuesByAttribute(metaAttribute, item.values, parseFormats);
            fullValue       = me.buildFullAttributeValue(formattedValue, formattedValues);

            if (useAttributeNameForDisplay) {
                fullValue = Ext.String.format('{0}: {1}', displayName, fullValue);
            }

            items.push(fullValue);
        });

        if (useAttributeNameForDisplay) {
            delimiter = ' ';
        } else {
            delimiter = ' | ';
        }

        str = items.join(delimiter);

        return str;
    },

    /**
     * Фильтр для отображаемых атрибутов
     *
     * @param attribute
     * @param {string[]} [displayAttributes] - атрибуты для отображения
     * @returns {boolean}
     */
    displayableAttributesFilter: function (attribute, displayAttributes) {
        var mainDisplayable = attribute.get('mainDisplayable'),
            attributeName = attribute.get('name');

        if (!Ext.isEmpty(displayAttributes)) {
            return (displayAttributes.indexOf(attributeName) !== -1);
        }

        return mainDisplayable;
    },

    buildFullAttributeValue: function (value, values) {
        var tpl,
            resut;

        tpl = Ext.create('Ext.Template' , [
            '{firstValue:htmlEncode}',
            ' ',
            '({otherwiseValues:htmlEncode})'
        ]);
        tpl.compile();

        if (Ext.isArray(values) && values.length > 1) {
            resut = tpl.apply({
                firstValue: Ext.Array.slice(values, 0, 1),
                otherwiseValues: Ext.Array.slice(values, 1).join(', ')
            });

        } else {
            resut = value;
        }

        return resut;
    },

    onAddSearchHitsButtonClick: function () {
        this.showSearchHitsSelectionWindow();
    },

    onDeleteSearchHitsButtonClick: function () {
        this.deleteSearchHits();
    },

    deleteSearchHits: function () {
        var searchHits;

        searchHits = this.getSelection();
        this.removeSearchHits(searchHits);
        this.selModel.deselectAll();
    },

    /**
     * @public
     */
    showSearchHitsSelectionWindow: function () {
        var metaRecord = this.getMetaRecord(),
            entityName,
            window,
            title;

        if (!metaRecord) {
            return;
        }

        entityName = metaRecord.get('name');
        title = this.buildSearchHitsSelectionWindowTitle();

        window = Ext.create('Unidata.view.component.search.SearchWindow', {
            toEntityDefaultDisplayAttributes: this.getToEntityDefaultDisplayAttributes(),
            selectedEntityName: entityName,
            entityReadOnly: true,
            title: title,
            listeners: {
                okbtnclick: this.onSearchHitsSelectionWindowOkButtonClick.bind(this)
            }
        });

        window.show();
    },

    buildSearchHitsSelectionWindowTitle: function () {
        var relation = this.getRelation(),
            title;

        title = Unidata.i18n.t('search>query.selectRecords') + ' "' + relation.get('displayName') + '"';

        return title;
    },

    onSearchHitsSelectionWindowOkButtonClick: function (wnd, searchHits) {
        this.addSearchHits(searchHits);
    },

    clearList: function () {
        var store = this.getStore();

        store.clearFilter();
        store.removeAll();
    },

    onSelectionChange: function (self, selected) {
        this.setSelectedCount(selected.length);
    }
});

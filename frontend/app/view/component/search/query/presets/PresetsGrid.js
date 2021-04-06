/**
 * Таблица сохраненных поисковых запросов
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.presets.PresetsGrid', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.component.search.query.presets.presetsgrid',

    requires: [
        'Unidata.model.search.QueryPreset'
    ],

    config: {
        selectedCount: 0,
        firstRecordsCount: 4,
        totalRecords: 0,
        entityName: null
    },

    hideHeaders: true,
    emptyText: Unidata.i18n.t('search>query.noRecords'),

    selModel: {
        selType: 'checkboxmodel',
        checkOnly: true,
        pruneRemoved: false,
        injectCheckbox: 'last'
    },

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
            }
        }
    },

    initComponent: function () {
        var QueryPresetStorage = Unidata.module.storage.QueryPresetStorage,
            store;

        this.dockedItems = this.buildDockedItems();
        this.createColumns();
        this.callParent(arguments);
        this.initReferences();
        store = this.createChainedStore(QueryPresetStorage.queryPresetStore);
        this.setStore(store);
        this.initListeners();
    },

    createChainedStore: function (source) {
        var store,
            me = this;

        store =  Ext.create('Ext.data.ChainedStore', {
            source: source,
            filters: [
                function (queryPreset) {
                    var entityName = me.getEntityName();

                    return queryPreset.get('entityName') === entityName;
                }
            ]
        });

        return store;
    },

    initListeners: function () {
        //this.getStore().on('datachanged', this.onStoreDataChanged, this);
        this.on('selectionchange', this.onSelectionChange, this);
    },

    initReferences: function () {

    },

    onStoreDataChanged: function () {
        var store = this.getStore(),
            count = store.getSource().getRange().length;

        this.setTotalRecords(count);
    },

    buildDockedItems: function () {
        var dockedItems;

        dockedItems = [{
            xtype: 'toolbar',
            reference: 'toolbar',
            dock: 'bottom',
            items: [
                {
                    xtype: 'un.simpleswitcher',
                    flex: 1,
                    reference: 'simpleSwitcher',
                    style: {
                        textAlign: 'center'
                    },
                    stateCfgs: [
                        {
                            title: Unidata.i18n.t('search>preset.showMore'),
                            handlerParams: {
                                handler: this.showAllRecords,
                                scope: this
                            }
                        },
                        {
                            title: Unidata.i18n.t('common:hide'),
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
                    xtype: 'container',
                    flex: 1,
                    bind: {
                        hidden: '{simpleSwitcherVisible}'
                    }
                },
                {
                    xtype: 'un.fontbutton',
                    reference: 'deleteSearchHitsButton',
                    iconCls: 'icon-trash2',
                    color: 'lightgray',
                    handler: this.onDeleteQueryPresetsButtonClick.bind(this),
                    tooltip: Unidata.i18n.t('search>preset.deleteTooltip'),
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
        var entityName = this.getEntityName();

        this.store.clearFilter(true);
        this.store.filter('entityName', entityName);
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
            width: 265,
            dataIndex: 'name',
            renderer: this.renderColumn.bind(this)
        }];
    },

    removePresets: function () {

    },

    /**
     * Рендеринг полей в основной колонке выдачи
     *
     * @param value
     * @param metadata
     * @param record
     * @returns {string|*}
     */
    renderColumn: function (value) {
        return '<span data-qtip="' + Ext.String.htmlEncodeMulti(value, 2) + '">' + Ext.String.htmlEncodeMulti(value, 1) + '</span>';
    },

    onDeleteQueryPresetsButtonClick: function (btn) {
        var title = Unidata.i18n.t('common:deleteNoun'),
            promptText = Unidata.i18n.t('search>preset.deletePrompt');

        Unidata.showPrompt(title, promptText, this.deleteQueryPresets, this, btn);
    },

    deleteQueryPresets: function () {
        var QueryPresetStorage = Unidata.module.storage.QueryPresetStorage,
            queryPresets       = this.getSelection();

        QueryPresetStorage.removeQueryPresets(queryPresets);
    },

    updateEntityName: function () {
        var entityName = this.getEntityName();

        this.store.clearFilter(true);
        this.store.filter('entityName', entityName);
    },

    onSelectionChange: function (self, selected) {
        this.setSelectedCount(selected.length);
    }
});

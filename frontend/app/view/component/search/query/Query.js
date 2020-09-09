/**
 * Поисковая панель (вынесена в отдельный компонент на базе существующей)
 *
 * Конфигурация:
 *
 * searchSectionVisible - видимость секций поисковой панели
 *   classifiers        - панель классификаторов
 *   relations          - панель связей
 *   errorsOnly         - чекбокс "Только ошибки"
 *   inactiveOnly       - чекбокс "Только удаленные"
 *   duplicatesOnly     - чекбокс "Только дубликаты"
 *
 * selectedEntityName   - имя выбранного реестра/справочника
 * entityReadOnly       - комбобокс выбора реестра/справочника доступен только "для чтения"
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.view.component.search.query.Query', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.String',
        'Unidata.view.component.search.query.QueryController',
        'Unidata.view.component.search.query.QueryModel',

        'Unidata.module.search.DataSearchQuery', // поисковой модуль

        'Unidata.view.component.search.attribute.FilterPanel',                  // панель поиска по атрибутам
        'Unidata.view.component.search.query.classifier.ClassifierFilterList',  // панель поиска по классификаторам
        'Unidata.view.component.search.query.relation.list.RelationSearchList', // панель поиска по связям
        'Unidata.view.component.search.query.dataquality.DataQualitySearch',     // панель поиска правилам качества

        'Unidata.view.component.search.query.presets.PresetsGrid',               // presets
        'Unidata.view.component.search.query.facet.FacetToggleButton'
    ],

    alias: 'widget.component.search.query',

    controller: 'component.search.query',
    viewModel: {
        type: 'component.search.query'
    },

    methodMapper: [
        {
            method: 'updateSearchQuery'
        },
        {
            method: 'updateTableSearch'
        },
        {
            method: 'getMetaRecord'
        },
        {
            method: 'getExtraParams'
        },
        {
            method: 'doSearch'
        },
        {
            method: 'updateSelectedEntityName'
        }
    ],

    header: {
        cls: 'un-search-panel-header'
    },

    cls: 'un-query',

    ui: 'un-search',

    reference: 'queryPanel',

    titleCollapse: true,

    config: {
        searchQuery: null,
        externalSearchSectionVisible: null,             // только для внешнего управления (значение инициализируется в initComponent)
        searchSectionVisible: null,                     // только для внутреннего управления (значение инициализируется в initComponent)
        toEntityDefaultDisplayAttributes: null,
        toEntityDefaultSearchAttributes: null,
        useToEntityDefaultSearchAttributes: false,
        selectedEntityName: null,
        entityReadOnly: false,
        tableSearch: false,         // признак запросов с табличным видом поиска
        useRouting: false
    },

    hideRelationsSearch: false,

    viewModelAccessors: [
        'searchSectionVisible',
        'entityReadOnly',
        'searchQuery'
    ],

    publishes: [
        'searchQuery' // для доступа в Resultset
    ],

    eventBusHolder: true,

    entityCombo: null,
    classifierFilterList: null,
    relationSearchList: null,
    queryButton: null,
    dataQualitySearch: null,

    store: null,

    fieldRouterComponents: null, // колллекция компонентов, участвующих в роутинге

    referenceHolder: true,

    searchErrorText: Unidata.i18n.t('search>query.searchError'),

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    defaults: {
        animCollapse: false,
        collapsible: true
    },

    items: [
        {
            xtype: 'panel',
            header: false,
            cls: 'un-query-pinned-sections',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'checkbox',
                    reference: 'useNewApi',
                    boxLabel: 'Использовать для поиска новый API',
                    labelAlign: 'right',
                    boxLabelAlign: 'after',
                    checked: true,
                    hidden: true
                },
                {
                    xtype: 'container',
                    hidden: true,
                    bind: {
                        hidden: '{defaultSearchAttributesSwitcherHidden}'
                    },
                    defaults: {
                        xtype: 'button',
                        ui: 'un-text-toggle-button',
                        color: 'toggle-text-gray',
                        allowDepress: false,
                        toggleGroup: 'defaultGroup'
                    },
                    items: [
                        {
                            text: Unidata.i18n.t('search>query.searchAttributes.overriden'),
                            toggleHandler: 'defaultDisplayAttributesSwitch',
                            reference: 'defaultDisplayAttributesSwitcherOverride'
                        },
                        {
                            text: Unidata.i18n.t('search>query.searchAttributes.default'),
                            reference: 'defaultDisplayAttributesSwitcherDefault'
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    ui: 'un-field-default',
                    itemId: 'queryTextfield',
                    reference: 'queryTextfield',
                    flex: 1,
                    hideLabel: true,
                    msgTarget: false,
                    validation: false,
                    enableKeyEvents: true,
                    emptyText: Unidata.i18n.t('search>query.keyword'),
                    listeners: {
                        change: 'onSearchTextFieldChange',
                        keypress: 'onSearchTextFieldKeyPress'
                    },
                    bind: {
                        disabled: '{!searchQuery.term.entity.name}',
                        value: '{searchQuery.term.text.value}'
                    },
                    disabled: true,
                    triggers: {
                        clear: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.setValue('');
                            }
                        }
                    }
                },
                {
                    xtype: 'un.entitycombo',
                    ui: 'un-field-default',
                    reference: 'entityCombo',
                    cls: 'entity',
                    hideLabel: true,
                    labelAlign: 'top',
                    autoSelect: true,
                    allowBlank: true,
                    forceSelection: true,
                    msgTarget: false,
                    emptyText: '- ' + Unidata.i18n.t('glossary:entityOrLookupEntity').toLowerCase() + ' -',
                    bind: {
                        value: '{searchQuery.term.entity.name}',
                        readOnly: '{entityReadOnly}'
                    }
                },
                {
                    xtype: 'button',
                    scale: 'large',
                    text: Unidata.i18n.t('common:search'),
                    iconCls: 'icon-magnifier',
                    reference: 'queryButton',
                    itemId: 'queryButton',
                    disabled: true,
                    bind: {
                        disabled: '{!searchQuery.term.entity.name}'
                    },
                    listeners: {
                        click: 'onSearchButtonClick'
                    }
                },
                {
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'top'
                    },
                    items: [{
                        xtype: 'button',
                        reference: 'clearSearchForm',
                        cls: 'un-query-extended-button',
                        scale: 'large',
                        text: Unidata.i18n.t('common:clear'),
                        listeners: {
                            click: 'onClearButtonClick'
                        }
                    },
                        {
                            xtype: 'container',
                            flex: 1
                        },
                        {
                            xtype: 'button',
                            reference: 'extendedSearch',
                            cls: 'un-query-extended-button',
                            scale: 'large',
                            text: Unidata.i18n.t('search>query.extra'),
                            listeners: {
                                click: 'onExtendedSearchClick'
                            }
                        }]
                }
            ]
        },
        {
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            scrollable: true,
            items: [
                {
                    xtype: 'panel',
                    collapsed: true,
                    header: false,
                    reference: 'systemWrap',
                    animCollapse: false,
                    items: {
                        xtype: 'panel',
                        animCollapse: false,
                        // cls: 'un-query-pinned-sections',
                        title: Unidata.i18n.t('search>query.system'),
                        ui: 'un-search-filter',
                        reference: 'systemSearchContainer',
                        collapsible: true,
                        titleCollapse: true,
                        collapsed: true,
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        listeners: {
                            beforeexpand: 'showExtended'
                        },
                        items: [
                            {
                                xtype: 'datefield',
                                ui: 'un-field-default',
                                fieldLabel: Unidata.i18n.t('common:onDate'),
                                labelWidth: 100,
                                reference: 'dateAsOf',
                                publishWithErrors: false,
                                bind: {
                                    value: '{searchQuery.term.dateAsOf.value}'
                                },
                                triggers: {
                                    clear: {
                                        cls: 'x-form-clear-trigger',
                                        handler: function () {
                                            this.setValue('');
                                        }
                                    }
                                },
                                listeners: {
                                    change: 'onChangeDateAsOf'
                                }
                            },
                            {
                                xtype: 'datefield',
                                ui: 'un-field-default',
                                fieldLabel: Unidata.i18n.t('common:created'),
                                labelWidth: 100,
                                reference: 'dateCreated',
                                publishWithErrors: false,
                                bind: {
                                    value: '{searchQuery.term.dateCreated.value}'
                                },
                                triggers: {
                                    clear: {
                                        cls: 'x-form-clear-trigger',
                                        handler: function () {
                                            this.setValue('');
                                        }
                                    }
                                }
                            },
                            {
                                xtype: 'datefield',
                                ui: 'un-field-default',
                                fieldLabel: Unidata.i18n.t('common:updated'),
                                labelWidth: 100,
                                reference: 'dateUpdated',
                                publishWithErrors: false,
                                bind: {
                                    value: '{searchQuery.term.dateUpdated.value}'
                                },
                                triggers: {
                                    clear: {
                                        cls: 'x-form-clear-trigger',
                                        handler: function () {
                                            this.setValue('');
                                        }
                                    }
                                }
                            },
                            {
                                xtype: 'combobox',
                                reference: 'operationType',
                                ui: 'un-field-default',
                                valueField: 'name',
                                displayField: 'displayName',
                                labelWidth: 100,
                                fieldLabel: Unidata.i18n.t('search>query.operationType'),
                                editable: false,
                                hidden: true,
                                bind: {
                                    hidden: '{operationTypeHidden}',
                                    value: '{searchQuery.term.facet.operation_type.value}'
                                },
                                store: {
                                    fields: [
                                        'name',
                                        'displayName'
                                    ],
                                    data: Unidata.Constants.getOperationTypes()
                                },
                                triggers: {
                                    clear: {
                                        cls: 'x-form-clear-trigger',
                                        handler: function () {
                                            this.setValue(null);
                                        }
                                    }
                                }
                            },
                            {
                                xtype: 'container',
                                layout: {
                                    type: 'vbox',
                                    align: 'stretch'
                                },
                                reference: 'searchOptionsContainer',
                                defaults: {
                                    margin: '0 0 5 0',
                                    labelAlign: 'right'
                                },
                                items: [
                                    {
                                        xtype: 'un.query.facettogglebutton',
                                        text: Unidata.i18n.t('search>query.allPeriodActual'),
                                        reference: 'allPeriodActual',
                                        disabled: true,
                                        bind: {
                                            pressed: '{searchQuery.term.facet.un_ranged.termIsActive}'
                                        },
                                        listeners: {
                                            toggle: 'onAllPeriodActualToggle'
                                        }
                                    },
                                    {
                                        xtype: 'un.query.facettogglebutton',
                                        text: Unidata.i18n.t('search>query.includeInactive'),
                                        reference: 'includeInactiveCheckbox',
                                        disabled: true,
                                        hidden: true,
                                        bind: {
                                            pressed: '{searchQuery.term.facet.include_inactive_periods.termIsActive}'
                                        },
                                        listeners: {
                                            toggle: 'onIncludeInactiveToggle'
                                        }
                                    },
                                    {
                                        xtype: 'un.query.facettogglebutton',
                                        text: Unidata.i18n.t('search>query.inactiveOnly'),
                                        reference: 'inactiveOnlyCheckbox',
                                        disabled: true,
                                        bind: {
                                            pressed: '{searchQuery.term.facet.inactive_only.termIsActive}',
                                            hidden: '{!searchSectionVisible.inactiveOnly}'
                                        },
                                        listeners: {
                                            toggle: 'onInactiveOnlyToggle'
                                        }
                                    },
                                    {
                                        xtype: 'un.query.facettogglebutton',
                                        text: Unidata.i18n.t('search>query.pendingOnly'),
                                        reference: 'pendingOnlyCheckbox',
                                        disabled: true,
                                        bind: {
                                            pressed: '{searchQuery.term.facet.pending_only.termIsActive}'
                                        },
                                        listeners: {
                                            toggle: 'onPendingOnlyToggle'
                                        }
                                    }
                                ]
                            },
                            {
                                xtype: 'checkbox',
                                boxLabel: Unidata.i18n.t('search>query.duplicatesOnly'),
                                margin: '0 0 5 86',
                                labelAlign: 'right',
                                boxLabelAlign: 'after',
                                reference: 'duplicatesOnlyCheckbox',
                                hidden: true,
                                listeners: {
                                    change: 'onChangeDuplicatesOnlyCheckbox'
                                },
                                bind: {
                                    value: '{searchQuery.term.facet.duplicates_only.termIsActive}'
                                }
                            }
                        ]
                    }
                },
                {
                    xtype: 'panel',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    reference: 'filtersContainer',
                    animCollapse: false,
                    // cls: 'un-query-scroll-sections',
                    collapsed: true,
                    header: false,
                    defaults: {
                        animCollapse: false,
                        listeners: {
                            change: 'onFilterChange',
                            beforeexpand: 'showExtended'
                        }
                    },
                    items: [
                        {
                            xtype: 'component.search.attribute.filterpanel',
                            reference: 'filterPanel',
                            bind: {
                                entityRecord: '{searchQuery.term.entity.metaRecord}',
                                searchQuery: '{searchQuery}',
                                hidden: '{!attributeFilterPanelVisible}'
                            }
                        },
                        {
                            xtype: 'component.search.query.classifierfilterlist',
                            reference: 'classifierFilterList',
                            listeners: {
                                change: 'onFilterChange',
                                allowedentitieschanged: 'onAllowedEntitiesChange'
                            },
                            bind: {
                                searchQuery: '{searchQuery}',
                                hidden: '{!searchSectionVisible.classifiers}'
                            }
                        },
                        {
                            xtype: 'component.search.query.dataquality.dataqualitysearch',
                            reference: 'dataQualitySearch',
                            bind: {
                                disabled: '{searchQuery.term.text.value}',
                                searchQuery: '{searchQuery}',
                                hidden: '{!searchSectionVisible.dataquality}'
                            }
                        },
                        {
                            xtype: 'component.search.query.relation.list.relationsearchlist',
                            reference: 'relationSearchList',
                            bind: {
                                metaRecord: '{searchQuery.term.entity.metaRecord}',
                                searchQuery: '{searchQuery}',
                                hidden: '{!searchSectionVisible.relations}'
                            }
                        }
                    ]
                }
            ],
            flex: 1
        },
        {
            xtype: 'panel',
            height: 210,
            cls: 'un-query-preset-panel',
            ui: 'un-search-filter',
            reference: 'queryPresetPanel',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            title: Unidata.i18n.t('search>preset.savedSearchQuery'),
            collapsible: true,
            collapsed: true,
            animCollapse: false,    //если включить, то происходят странные визуальные эффекты
            titleCollapse: true,
            tools: [
                {
                    xtype: 'un.fontbutton.additem',
                    reference: 'addQueryPresetButton',
                    handler: 'onAddQueryPreset',
                    color: 'lightgray',
                    tooltip: Unidata.i18n.t('search>query.saveCurrentQueryParams'),
                    bind: {
                        disabled: '{!searchQuery.term.entity.metaRecord}'
                    }
                }
            ],
            items: [
                {
                    xtype: 'component.search.query.presets.presetsgrid',
                    reference: 'queryPresetGrid',
                    flex: 1,
                    cls: 'un-result-grid un-query-preset-grid',
                    bind: {
                        entityName: '{searchQuery.term.entity.name}'
                    },
                    listeners: {
                        cellclick: 'onQueryPresetItemClick'
                    }
                }
            ]
        }
    ],

    initComponent: function () {
        this.fieldRouterComponents = new Ext.util.Collection();

        this.setExternalSearchSectionVisible({
            classifiers: true,
            dataquality: true,
            relations: true,
            errorsOnly: true,
            inactiveOnly: true,
            duplicatesOnly: true
        });

        this.setExternalSearchSectionVisible({
            classifiers: true,
            dataquality: true,
            relations: true,
            errorsOnly: true,
            inactiveOnly: true,
            duplicatesOnly: true
        });

        this.callParent(arguments);
        this.initReferences();
        this.createStore();
        this.initSearchQuery();
        this.initData();
    },

    initReferences: function () {
        this.entityCombo          = this.lookupReference('entityCombo');
        this.classifierFilterList = this.lookupReference('classifierFilterList');
        this.relationSearchList = this.lookupReference('relationSearchList');
        this.queryButton = this.lookupReference('queryButton');
        this.filterPanel = this.lookupReference('filterPanel');
        this.defaultDisplayAttributesSwitcherOverride = this.lookupReference('defaultDisplayAttributesSwitcherOverride');
        this.defaultDisplayAttributesSwitcherDefault = this.lookupReference('defaultDisplayAttributesSwitcherDefault');
        this.dataQualitySearch = this.lookupReference('dataQualitySearch');
        this.queryPresetGrid = this.lookupReference('queryPresetGrid');
    },

    initData: function () {
        var useToEntityDefaultSearchAttributes = this.getUseToEntityDefaultSearchAttributes();

        this.filterPanel.setToEntityDefaultSearchAttributes(this.getToEntityDefaultSearchAttributes());
        this.filterPanel.setUseToEntityDefaultSearchAttributes(this.getUseToEntityDefaultSearchAttributes());
        this.defaultDisplayAttributesSwitcherOverride.setPressed(useToEntityDefaultSearchAttributes);
        this.defaultDisplayAttributesSwitcherDefault.setPressed(!useToEntityDefaultSearchAttributes);
    },

    onDestroy: function () {
        this.entityCombo = null;
        this.classifierFilterList = null;
        this.relationSearchList = null;
        this.queryButton = null;
        this.filterPanel = null;
        this.defaultDisplayAttributesSwitcherOverride = null;
        this.defaultDisplayAttributesSwitcherDefault = null;
        this.dataQualitySearch = null;

        this.callParent(arguments);
    },

    /**
     * Инициализация поискового модуля
     */
    initSearchQuery: function () {
        if (!this.getSearchQuery()) {
            this.setSearchQuery(new Unidata.module.search.DataSearchQuery());
        }
    },

    createStore: function () {
        this.store = Ext.create('Ext.data.Store', {
                model: 'Unidata.model.search.SearchHit',
                pageSize: Unidata.Config.getCustomerCfg()['SEARCH_ROWS'],
                // прокси подменяется во время поиска searchproxysimple | searchproxyform
                proxy: {
                    type: 'data.searchproxysimple'
                }
            }
        );

        this.originalStoreProxy = this.store.getProxy();

        return this.store;
    },

    /**
     * Собираем компоненты с плагином form.field.router в коллекцию,
     * для дальнейшего использования в роутинге
     * @see Unidata.view.component.search.query.QueryController.initRouterAfterRender
     * @param components
     */
    componentsAdded: function (components) {
        this.callParent(arguments);

        Ext.Array.each(components, function (component) {
            if (component.findPlugin('form.field.router')) {
                this.fieldRouterComponents.add(component);

                component.on('destroy', function (component) {
                    this.fieldRouterComponents.remove(component);
                }, this);
            }
        }, this);
    },

    updateToEntityDefaultSearchAttributes: function (toEntityDefaultSearchAttributes) {
        var defaultSearchAttributesSwitcherHidden = true;

        if (Ext.isArray(toEntityDefaultSearchAttributes) && toEntityDefaultSearchAttributes.length) {
            defaultSearchAttributesSwitcherHidden = false;
        }

        this.getViewModel().set('defaultSearchAttributesSwitcherHidden', defaultSearchAttributesSwitcherHidden);

        this.updateEntityTermSearchFields();
    },

    updateUseToEntityDefaultSearchAttributes: function (flag) {
        if (!this.isConfiguring) {
            this.defaultDisplayAttributesSwitcherOverride.setPressed(flag);
            this.defaultDisplayAttributesSwitcherDefault.setPressed(!flag);
        }

        this.updateEntityTermSearchFields();
    },

    updateEntityTermSearchFields: function () {
        var me = this,
            viewModel = this.getViewModel(),
            binding;

        binding = viewModel.bind('{searchQuery.term.entity}', function (entityTerm) {
            var filterPanel = me.filterPanel,
                toEntityDefaultSearchAttributes,
                useToEntityDefaultSearchAttributes;

            if (me.isDestroyed) {
                binding.destroy();

                return;
            }

            if (entityTerm) {
                useToEntityDefaultSearchAttributes = me.getUseToEntityDefaultSearchAttributes();
                toEntityDefaultSearchAttributes = me.getToEntityDefaultSearchAttributes();

                if (useToEntityDefaultSearchAttributes &&
                    toEntityDefaultSearchAttributes &&
                    toEntityDefaultSearchAttributes.length) {

                    filterPanel.setUseToEntityDefaultSearchAttributes(true);
                    filterPanel.setToEntityDefaultSearchAttributes(toEntityDefaultSearchAttributes);
                    entityTerm.setSearchFields(toEntityDefaultSearchAttributes);
                } else {
                    filterPanel.setUseToEntityDefaultSearchAttributes(false);
                    entityTerm.setSearchFields(null);
                }

                binding.destroy();
            }
        });
    },

    /**
     * Привязать resultsetPanel для отображения результатов поиска
     * Эта панель должна предоставлять метод setSourceStore
     *
     * @param resultsetPanel
     */
    linkResultsetPanel: function (resultsetPanel) {
        this.resultsetPanel = resultsetPanel;

        resultsetPanel.setSourceStore(this.store);
    }
});

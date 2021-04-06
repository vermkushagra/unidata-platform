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

        'Unidata.view.component.search.attribute.FilterPanel',                  // панель поиска по атрибутам
        'Unidata.view.component.search.query.classifier.ClassifierFilterList',  // панель поиска по классификаторам
        'Unidata.view.component.search.query.relation.list.RelationSearchList', // панель поиска по связям
        'Unidata.view.component.search.query.dataquality.DataQualitySearch',     // панель поиска правилам качества

        'Unidata.view.component.search.query.presets.PresetsGrid'               // presets
    ],

    alias: 'widget.component.search.query',

    controller: 'component.search.query',
    viewModel: {
        type: 'component.search.query'
    },

    methodMapper: [
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
        searchSectionVisible: {
            classifiers: true,
            dataquality: true,
            relations: true,
            errorsOnly: true,
            inactiveOnly: true,
            duplicatesOnly: true
        },
        toEntityDefaultDisplayAttributes: null,
        selectedEntityName: null,
        entityReadOnly: false,
        attributeTabletsCount: 0,
        tableSearch: false,         // признак запросов с табличным видом поиска
        useRouting: false
    },

    hideRelationsSearch: false,

    viewModelAccessors: ['searchSectionVisible', 'entityReadOnly', 'attributeTabletsCount'],

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

    items: [
        {
            xtype: 'container',
            cls: 'un-query-pinned-sections',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
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
                    listeners: {
                        change: 'onEntityChange'
                    },
                    bind: {
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
            reference: 'placeholderContainer',
            cls: 'un-query-extended-sections-placeholder',
            hidden: false,
            flex: 1
        },
        {
            xtype: 'container',
            reference: 'extendedSearchContainer',
            hidden: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            cls: 'un-query-extended-sections',
            items: [
                {
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
                        expand: 'onContentExpand'
                    },
                    items: [
                        {
                            xtype: 'datefield',
                            ui: 'un-field-default',
                            fieldLabel: Unidata.i18n.t('common:onDate'),
                            labelWidth: 70,
                            reference: 'dateAsOf',
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
                            labelWidth: 70,
                            reference: 'dateCreated',
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
                            labelWidth: 70,
                            reference: 'dateUpdated',
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
                            xtype: 'container',
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            reference: 'searchOptionsContainer',
                            defaults: {
                                margin: '0 0 5 75',
                                labelAlign: 'right',
                                boxLabelAlign: 'after'
                            },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    boxLabel: Unidata.i18n.t('search>query.allPeriodActual'),
                                    margin: '0 0 5 75',
                                    labelAlign: 'right',
                                    boxLabelAlign: 'after',
                                    reference: 'allPeriodActual',
                                    disabled: true,
                                    // checked: true,
                                    listeners: {
                                        change: 'onChangeAllPeriodActualCheckbox'
                                    }
                                },
                                {
                                    xtype: 'checkbox',
                                    boxLabel: Unidata.i18n.t('search>query.inactiveOnly'),
                                    reference: 'inactiveOnlyCheckbox',
                                    disabled: true,
                                    bind: {
                                        hidden: '{!searchSectionVisible.inactiveOnly}'
                                    }
                                },
                                {
                                    xtype: 'checkbox',
                                    boxLabel: Unidata.i18n.t('search>query.pendingOnly'),
                                    reference: 'pendingOnlyCheckbox',
                                    hidden: true,
                                    disabled: true
                                }
                            ]
                        },
                        {
                            xtype: 'checkbox',
                            boxLabel: Unidata.i18n.t('search>query.duplicatesOnly'),
                            margin: '0 0 5 75',
                            labelAlign: 'right',
                            boxLabelAlign: 'after',
                            reference: 'duplicatesOnlyCheckbox',
                            disabled: true,
                            listeners: {
                                change: 'onChangeDuplicatesOnlyCheckbox'
                            },
                            bind: {
                                hidden: '{!searchSectionVisible.duplicatesOnly}'
                            }
                        }
                    ]
                },
                {
                    xtype: 'container',
                    reference: 'filtersContainer',
                    cls: 'un-query-scroll-sections',
                    autoScroll: true,
                    flex: 1,
                    defaults: {
                        animCollapse: false,
                        listeners: {
                            change: 'onFilterChange',
                            beforeexpand: 'onContentExpand'
                        }
                    },
                    items: [
                        {
                            xtype: 'component.search.attribute.filterpanel',
                            reference: 'filterPanel',
                            bind: {
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
                                hidden: '{!searchSectionVisible.classifiers}'
                            }
                        },
                        {
                            xtype: 'component.search.query.dataquality.dataqualitysearch',
                            reference: 'dataQualitySearch',
                            bind: {
                                hidden: '{!searchSectionVisible.dataquality}'
                            }
                        },
                        {
                            xtype: 'component.search.query.relation.list.relationsearchlist',
                            reference: 'relationSearchList',
                            bind: {
                                hidden: '{!searchSectionVisible.relations}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'panel',
            height: 210,
            cls: 'un-query-preset-panel',
            ui: 'un-search-filter',
            reference: 'queryPresetPanel',
            bind: {
                hidden: '{!queryPresetPanelVisible}'
            },
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
                    tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:record')}),
                    bind: {
                        disabled: '{!metarecord}'
                    }
                }
            ],
            items: [
                {
                    xtype: 'component.search.query.presets.presetsgrid',
                    reference: 'queryPresetGrid',
                    flex: 1,
                    cls: 'un-result-grid un-query-preset-grid',
                    listeners: {
                        cellclick: 'onQueryPresetItemClick'
                    }
                }
            ]
        }
    ],

    initComponent: function () {
        this.fieldRouterComponents = new Ext.util.Collection();

        this.callParent(arguments);
        this.initReferences();
        this.createStore();
    },

    initReferences: function () {
        this.entityCombo          = this.lookupReference('entityCombo');
        this.classifierFilterList = this.lookupReference('classifierFilterList');
        this.relationSearchList = this.lookupReference('relationSearchList');
        this.queryButton = this.lookupReference('queryButton');
        this.filterPanel = this.lookupReference('filterPanel');
        this.dataQualitySearch = this.lookupReference('dataQualitySearch');
        this.queryPresetGrid = this.lookupReference('queryPresetGrid');
    },

    onDestroy: function () {
        this.entityCombo = null;
        this.classifierFilterList = null;
        this.relationSearchList = null;
        this.queryButton = null;
        this.filterPanel = null;
        this.dataQualitySearch = null;

        this.callParent(arguments);
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

    /**
     * Привязать resultsetPanel для отображения результатов поиска
     * Эта панель должна предоставлять метод setSourceStore
     *
     * @param resultsetPanel
     */
    linkResultsetPanel: function (resultsetPanel) {
        resultsetPanel.setSourceStore(this.store);
    }
});

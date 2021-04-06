/**
 * Панель результатов поиска данных
 *
 * @author Sergey Shishigin
 * @date 2016-10-26
 */
Ext.define('Unidata.view.component.search.resultset.Resultset', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.search.resultset.ResultsetController',
        'Unidata.view.component.search.resultset.ResultsetModel',

        'Unidata.view.component.toolbar.ResultPaging'
    ],

    alias: 'widget.component.search.resultset',

    viewModel: {
        type: 'component.search.resultset'
    },
    controller: 'component.search.resultset',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-result',

    referenceHolder: true,

    bind: {
        title: Ext.String.format('{0} ({1})', Unidata.i18n.t('search>resultset.results'), '{resultsetCount:number("0,000")}')
    },

    methodMapper: [
        {
            method: 'deselectAll'
        },
        {
            method: 'setSearchHits'
        },
        {
            method: 'updateSourceStore'
        },
        {
            method: 'getSelectedSearchHits'
        },
        {
            method: 'refreshPaging'
        },
        {
            method: 'clearResultset'
        },
        {
            method: 'createChainedStore'
        },
        {
            method: 'updateMetaRecord'
        },
        {
            method: 'updateAllPeriodSearch'
        }
    ],

    config: {
        queryPanel: null,
        metaRecord: null,
        sourceStore: null,
        resultsetCount: 0,
        isPagingEnable: false,
        enableSelectionQueryMode: true,
        allPeriodSearch: false,
        toEntityDefaultDisplayAttributes: null
    },

    viewModelAccessors: ['resultsetCount', 'isPagingEnable', 'enableSelectionQueryMode', 'metaRecord'],

    resultsetGrid: null,
    pagingToolbar: null,

    toolTip: null, // тултип отображающий подсказку для неумещающихся значений

    tbar: {
        items: [
            {
                xtype: 'un.resultpaging',
                reference: 'pagingToolbar',
                bind: {
                    hidden: '{!isPagingEnable}'
                },
                displayInfo: false,
                flex: 1,
                emptyMsg: '- ' + Unidata.i18n.t('search>query.noRecords') + ' -'
            },
            {
                xtype: 'combobox',
                ui: 'un-field-default',
                reference: 'pageSize',
                displayField: 'value',
                queryMode: 'local',
                editable: false,
                width: 65,
                bind: {
                    hidden: '{!isPagingEnable}'
                },
                listeners: {
                    change: 'onPageSizeChange'
                }
            }
        ]
    },
    items: [
        {
            xtype: 'grid',
            reference: 'resultsetGrid',
            cls: 'un-result-grid',
            hideHeaders: true,
            deferEmptyText: false,
            emptyText: Unidata.i18n.t('search>resultset.empty'),
            flex: 1,
            selModel: {
                selType: 'checkboxmodel',
                checkOnly: true,
                pruneRemoved: false,
                injectCheckbox: 'last'
            },
            listeners: {
                selectionchange: 'onSelectionchange',
                beforeitemclick: 'onBeforeitemclick',
                viewready: 'onResultSetGridViewReady'
            },
            columns: [
                {
                    resizable: false,
                    sortable: false,
                    hideable: false,
                    focusable: false,
                    border: false,
                    flex: 1,
                    dataIndex: 'preview',
                    renderer: 'renderColumn'
                }
            ]
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            layout: {
                type: 'hbox',
                pack: 'center',
                align: 'middle'
            },
            items: [
                {
                    xtype: 'container',
                    hidden: true,
                    bind: {
                        hidden: '{!selectionMode}'
                    },
                    defaults: {
                        margin: 10,
                        buttonSize: 'medium'
                    },
                    items: [
                        {
                            xclass: 'Unidata.view.component.button.RoundButton',
                            reference: 'selectAllButton',
                            iconCls: 'icon-files',
                            tooltip: Unidata.i18n.t('search>resultset.selectAll'),
                            listeners: {
                                click: 'selectAll'
                            },
                            bind: {
                                hidden: '{!enableSelectionQueryMode}'
                            },
                            color: 'gray'
                        },
                        {
                            xclass: 'Unidata.view.component.button.RoundButton',
                            iconCls: 'icon-file-check',
                            tooltip: Unidata.i18n.t('search>resultset.selectPage'),
                            listeners: {
                                click: 'selectPage'
                            },
                            color: 'gray'
                        },
                        {
                            xclass: 'Unidata.view.component.button.RoundButton',
                            iconCls: 'icon-prohibited',
                            tooltip: Unidata.i18n.t('search>resultset.deselectAll'),
                            listeners: {
                                click: 'deselectAll'
                            },
                            color: 'gray'
                        }
                    ]
                },
                {
                    xclass: 'Unidata.view.component.button.RoundButton',
                    iconCls: 'icon-hdd-down',
                    tooltip: Unidata.i18n.t('common:importData'),
                    margin: '10 10 10 0',
                    buttonSize: 'medium',
                    listeners: {
                        click: 'onDataImportClick'
                    },
                    hidden: true,
                    bind: {
                        hidden: '{importDataButtonHidden}'
                    },
                    color: 'gray'
                }
            ]
        }
    ],

    listeners: {
        selectionchange: 'updateSelectionMode'
    },

    editModeType: {
        NONE: false,            // без режимов
        SELECTION: 'selection', // выделение отдельных записей
        QUERY: 'query',         // выделение всех(поисковой запрос)
        DISABLED: 'disabled'    // выделение отключено
    },

    currentEditModeType: false, // текущий режим редактирования

    onDestroy: function () {
        Ext.destroy(this.toolTip);
        this.toolTip = null;

        this.callParent(arguments);
    },

    isEditModeDisabled: function () {
        return this.currentEditModeType == this.editModeType.DISABLED;
    },

    setEditMode: function (type) {
        if (this.currentEditModeType) {
            this.removeCls('un-editmode-' + this.currentEditModeType);
        }

        this.currentEditModeType = type;

        if (type) {
            this.addCls('un-editmode-' + type);
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
        this.initListeners();
        this.initPageSizeStore();
    },

    initPageSizeStore: function () {
        var pageSizeCombobox = this.lookupReference('pageSize'),
            dataValues = [Unidata.Config.getCustomerCfg()['SEARCH_ROWS']],
            store;

        dataValues.push(Math.ceil(dataValues[0] * 0.16666) * 10);
        dataValues.push(Math.ceil(dataValues[1] * 0.14) * 10);

        Ext.Array.each(dataValues, function (value, index, arr) {
            arr[index] = {value: value};
        });

        store = Ext.create('Ext.data.Store', {
            fields: ['value'],
            data: dataValues
        });

        pageSizeCombobox.setStore(store);
        pageSizeCombobox.setValue(Unidata.Config.getCustomerCfg()['SEARCH_ROWS']);
    },

    initReferences: function () {
        this.resultsetGrid = this.lookupReference('resultsetGrid');
        this.pagingToolbar = this.lookupReference('pagingToolbar');
    },

    initListeners: function () {
        var resultsetGrid = this.resultsetGrid;

        this.relayers = this.relayEvents(resultsetGrid, ['itemclick']);
    }
});

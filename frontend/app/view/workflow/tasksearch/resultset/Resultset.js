/**
 * @author Aleksandr Bavin
 * @date 06.06.2016
 */
Ext.define('Unidata.view.workflow.tasksearch.resultset.Resultset', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.workflow.tasksearch.query.Query',
        'Unidata.view.workflow.tasksearch.resultset.ResultsetController'
    ],

    alias: 'widget.workflow.tasksearch.resultset',

    controller: 'resultset',

    ui: 'un-result',

    collapsible: false,
    collapseDirection: 'left',
    collapseMode: 'header',
    animCollapse: false,
    titleCollapse: true,
    width: 300,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        searchType: Unidata.view.workflow.tasksearch.query.Query.SEARCH_MY,
        resultType: null
    },

    methodMapper: [
        {
            method: 'changeSearchTypeName'
        },
        {
            method: 'onMyTaskCountChange'
        },
        {
            method: 'onAvailableTaskCountChange'
        },
        {
            method: 'initCounter'
        }
    ],

    items: [
        {
            xtype: 'grid',
            reference: 'searchResultGrid',
            cls: 'un-result-grid',

            session: true,

            hideHeaders: true,
            flex: 1,
            disableSelection: true,

            emptyText: Unidata.i18n.t('workflow>tasksearch.noData'),

            columns: [
                {
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    dataIndex: 'taskId',
                    renderer: 'columnRenderer',
                    flex: 1
                }
            ],
            dockedItems: [{
                xtype: 'pagingtoolbar',
                displayInfo: false,
                bind: {
                    hidden: '{isPagingHidden}',
                    store: '{taskSearchHitStore}'
                },
                hideRefreshButton: true,
                dock: 'top'
            }],
            bind: {
                store: '{taskSearchHitStore}'
            },
            listeners: {
                rowclick: 'onRowClick',
                rowdblclick: 'onRowClick'
            }
        }
    ],

    header: {
        // заголовок стоит последним элементов и скрывается за границей header, т.к. он не нужен
        titlePosition: 2
    },

    tools: [
        {
            xtype: 'combobox',
            ui: 'un-field-default',
            reference: 'searchTypeComboBox',
            displayField: 'searchTypeName',
            valueField: 'searchType',
            queryMode: 'local',
            editable: false,
            submitValue: false,
            margin: '0 15 0 0',
            width: 250,
            value: Unidata.view.workflow.tasksearch.query.Query.SEARCH_MY,
            store: {
                fields: ['searchType', 'searchTypeName'],
                data: [
                    {
                        searchType: Unidata.view.workflow.tasksearch.query.Query.SEARCH_AVAILABLE,
                        searchTypeName: Unidata.view.workflow.tasksearch.query.Query.defaultSearchTypeNames[Unidata.view.workflow.tasksearch.query.Query.SEARCH_AVAILABLE]
                    },
                    {
                        searchType: Unidata.view.workflow.tasksearch.query.Query.SEARCH_MY,
                        searchTypeName: Unidata.view.workflow.tasksearch.query.Query.defaultSearchTypeNames[Unidata.view.workflow.tasksearch.query.Query.SEARCH_MY]
                    },
                    {
                        searchType: Unidata.view.workflow.tasksearch.query.Query.SEARCH_COMPLEX,
                        searchTypeName: Unidata.i18n.t('workflow>tasksearch.title')
                    },
                    {
                        searchType: Unidata.view.workflow.tasksearch.query.Query.SEARCH_HISTORICAL,
                        searchTypeName: Unidata.i18n.t('workflow>tasksearch.history')
                    }
                ]
            },
            listeners: {
                change: 'onSearchSwitcherChange',
                render: 'onComboBoxRender'
            }
        },
        {
            type: 'refresh',
            handler: 'onRefreshButtonClick',
            tooltip: Unidata.i18n.t('common:refresh')
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        this.initListeners();
    },

    updateSearchType: function () {
        if (this.rendered) {
            this.updatesearchTypeComboBox();
        } else {
            this.on('render', this.updatesearchTypeComboBox, this);
        }
    },

    updatesearchTypeComboBox: function () {
        var searchType = this.getSearchType();

        if (Ext.isEmpty(searchType)) {
            return;
        }

        this.lookupReference('searchTypeComboBox').setValue(searchType);
    },

    initListeners: function () {
        var eventBus,
            poller;

        poller = Unidata.module.poller.TaskCountPoller.getInstance();

        eventBus = poller.pollerEventBus;

        eventBus.on('changeavailablecount', this.onAvailableTaskCountChange, this);
        eventBus.on('changemycount', this.onMyTaskCountChange, this);
    }
});

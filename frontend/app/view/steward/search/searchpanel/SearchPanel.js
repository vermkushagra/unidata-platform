Ext.define('Unidata.view.steward.search.searchpanel.SearchPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.search.searchpanel.SearchPanelController',
        'Unidata.view.steward.search.searchpanel.SearchPanelModel',

        'Unidata.view.component.search.query.Query',
        'Unidata.view.component.search.resultset.Resultset',

        'Unidata.view.steward.search.bulk.wizard.Wizard',
        'Unidata.view.steward.search.recordshow.Recordshow',
        'Unidata.view.steward.search.tableresultset.TableResultset'

    ],

    alias: 'widget.steward.search.searchpanel',

    controller: 'steward.search.searchpanel',
    viewModel: {
        type: 'steward.search.searchpanel'
    },

    cls: 'un-section-data',

    referenceHolder: true,

    methodMapper: [
        {
            method: 'doSearch'
        },
        {
            method: 'getSelectedSearchHits'
        },
        {
            method: 'initRelayers'
        },
        {
            method: 'buildResultsetPanelTools'
        },
        {
            method: 'buildQueryPanelTools'
        }
    ],

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    listeners: {
        destroy: 'onDestroy'
    },

    queryPanel: null,
    resulsetPanel: null,
    tableResultsetPanel: null,
    recordshowTabPanel: null,

    items: [
        {
            xtype: 'component.search.query',
            reference: 'queryPanel',
            title: Unidata.i18n.t('search>query.title'),
            collapsible: true,
            collapseDirection: 'left',
            collapseMode: 'header',
            titleCollapse: true,
            animCollapse: false,
            width: 300,
            listeners: {
                searchsuccess: 'onSearchSuccess',
                entitychange: 'onEntityChange',
                facetschange: 'onFacetsChange'
            },
            useRouting: true
        },
        {
            xtype: 'component.search.resultset',
            reference: 'resultsetPanel',
            collapsible: true,
            collapseDirection: 'left',
            collapseMode: 'header',
            titleCollapse: true,
            animCollapse: false,
            bind: {
                searchQuery: '{queryPanel.searchQuery}'
            },
            listeners: {
                itemclick: 'onResultSetItemClick',
                selectionchange: 'onSelectionChange'
            },
            width: 300
        },
        {
            xtype: 'steward.search.recordshow',
            reference: 'recordshowTabPanel',
            listeners: {
                recordsave: 'onRecordChanged',
                recorddelete: 'onRecordChanged',
                recordmerged: 'onRecordChanged',
                clusterchanged: 'onRecordChanged',
                wantaddetalonid: 'onWantAddEtalonId',
                recorddecline: 'onRecordChanged',
                etalonitemclick: 'onItemResultClick'
            },
            flex: 1
        }
    ],

    initComponent: function () {
        var queryPanel;

        this.callParent(arguments);
        this.initReferences();
        this.initRelayers();

        this.resultsetPanel.tools = this.buildResultsetPanelTools();
        this.queryPanel.tools = this.buildQueryPanelTools();

        queryPanel = this.queryPanel;
        queryPanel.linkResultsetPanel(this.resultsetPanel);
    },

    initReferences: function () {
        this.queryPanel = this.lookupReference('queryPanel');
        this.resultsetPanel = this.lookupReference('resultsetPanel');
        this.resultsetPanel.setQueryPanel(this.queryPanel);
        this.recordshowTabPanel = this.lookupReference('recordshowTabPanel');
    },

    /**
     * Подчищаем за собой
     */
    onDestroy: function () {
        this.queryPanel = null;
        this.resultsetPanel = null;
        this.recordshowTabPanel = null;

        this.callParent(arguments);
    }
});

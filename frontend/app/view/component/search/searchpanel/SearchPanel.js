Ext.define('Unidata.view.component.search.searchpanel.SearchPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.search.searchpanel.SearchPanelController',
        'Unidata.view.component.search.searchpanel.SearchPanelModel',

        'Unidata.view.component.search.query.Query',
        'Unidata.view.component.search.resultset.Resultset'
    ],

    alias: 'widget.component.search.searchpanel',

    controller: 'component.search.searchpanel',
    viewModel: {
        type: 'component.search.searchpanel'
    },

    methodMapper: [
        {
            method: 'getSelectedSearchHits'
        },
        {
            method: 'initRelayers'
        },
        {
            method: 'updateSelectedEntityName'
        },
        {
            method: 'updateToEntityDefaultDisplayAttributes'
        },
        {
            method: 'updateEntityReadOnly'
        }
    ],

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    cls: 'animated fadeIn',

    listeners: {
        destroy: 'onDestroy'
    },

    config: {
        toEntityDefaultDisplayAttributes: null,
        selectedEntityName: null,
        entityReadOnly: null
    },

    queryPanel: null,
    resulsetPanel: null,

    items: [
        {
            xtype: 'component.search.query',
            title: Unidata.i18n.t('search>query.title'),
            reference: 'queryPanel',
            hideRelationsSearch: true,
            width: 300,
            listeners: {
                entitychange: 'onEntityChange'
            }
        },
        {
            xtype: 'component.search.resultset',
            reference: 'resultsetPanel',
            width: 300,
            enableSelectionQueryMode: false
        }
    ],

    /**
     * Растягивает панель результатов поиска
     */
    flexResultSetPanel: function () {
        this.resultsetPanel.setFlex(1);

        this.updateLayout();
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
        this.initRelayers();

        this.queryPanel.linkResultsetPanel(this.resultsetPanel);
    },

    initReferences: function () {
        this.queryPanel = this.lookupReference('queryPanel');
        this.resultsetPanel = this.lookupReference('resultsetPanel');
    },

    onDestroy: function () {
        this.callParent(arguments);

        this.queryPanel = null;
        this.resultsetPanel = null;
    }
});

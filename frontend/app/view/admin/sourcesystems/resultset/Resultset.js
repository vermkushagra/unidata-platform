Ext.define('Unidata.view.admin.sourcesystems.resultset.Resultset', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.sourcesystems.resultset.ResultsetController',
        'Unidata.view.admin.sourcesystems.resultset.ResultsetModel'
    ],

    alias: 'widget.admin.sourcesystems.resultset',

    viewModel: {
        type: 'admin.sourcesystems.resultset'
    },
    controller: 'admin.sourcesystems.resultset',

    config: {
        readOnly: false
    },

    methodMapper: [
        {
            method: 'updateReadOnly'
        }
    ],

    referenceHolder: true,

    width: 300,
    ui: 'un-result',

    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    padding: 0,
    bodyPadding: 0,
    collapsible: true,
    collapseDirection: 'left',
    collapseMode: 'header',
    titleCollapse: true,
    floatable: false,

    tbar: {
        xtype: 'pagingtoolbar',
        reference: 'pagingToolbar',
        bind: {
            store: '{resultsetStore}',
            hidden: '{!isPagingEnable}'
        },
        cls: 'paging-toolbar',
        displayInfo: false,
        emptyMsg: Unidata.i18n.t('admin.common>noRecords'),
        hideRefreshButton: true
    },
    items: [
        {
            xtype: 'grid',
            reference: 'resultsetGrid',
            hideHeaders: true,
            focusable: false,
            deferEmptyText: false,
            emptyText: Unidata.i18n.t('search>resultset.empty'),
            flex: 1,
            bind: {
                store: '{resultsetStore}'
            },
            cls: 'un-result-grid',
            columns: [
                {
                    resizable: true,
                    menuDisabled: true,
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

    bind: {
        title: Unidata.i18n.t('glossary:dataSources')
    },

    tools: [
        {
            type: 'plus',
            handler: 'onAddRecordButtonClick',
            tooltip: Unidata.i18n.t('admin.sourcesystems>addSourceSystem'),
            bind: {
                hidden: '{!createSourceSystemButtonVisible}'
            }
        }
    ]
});

/**
 * Панель результатов поиска кластеров
 *
 * @author Ivan Marshalkin
 * @date 2016-10-26
 */
Ext.define('Unidata.view.steward.cluster.list.ClusterList', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.cluster.list.ClusterListController',
        'Unidata.view.steward.cluster.list.ClusterListModel',

        'Unidata.view.component.toolbar.ResultPaging'
    ],

    alias: 'widget.steward.cluster.listview',

    viewModel: {
        type: 'steward.cluster.listview'
    },
    controller: 'steward.cluster.listview',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-result',

    referenceHolder: true,

    bind: {
        title: Ext.String.format('{0} ({1})', Unidata.i18n.t('cluster>results'), '{totalCount:number("0,000")}')
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'displayClusterList'
        },
        {
            method: 'reloadClusterList'
        }
    ],

    config: {},

    gridClusterList: null,                  // ссылка на грид списка кластеров
    pagingClusterList: null,                // ссылка на пейджинг списка кластеров

    loadClusterListErrorText: Unidata.i18n.t('cluster>loadClusterListError'),

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.gridClusterList   = this.lookupReference('gridClusterList');
        me.pagingClusterList = this.lookupReference('pagingClusterList');
    },

    onDestroy: function () {
        var me = this;

        me.gridClusterList = null;
        me.pagingClusterList = null;

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'grid',
            reference: 'gridClusterList',
            bind: {
                store: '{clusterListStore}'
            },
            cls: 'un-result-grid',
            flex: 1,
            hideHeaders: true,
            scrollable: 'vertical',
            disableSelection: true,
            columns: [
                {
                    text: Unidata.i18n.t('cluster>ofCluster'),
                    dataIndex: 'ruleName',
                    width: 100,
                    sortable: false,
                    menuDisabled: true,
                    flex: 1,
                    renderer: 'ruleNameRenderer'
                }
            ],
            dockedItems: [{
                xtype: 'un.resultpaging',
                reference: 'pagingClusterList',
                bind: {
                    store: '{clusterListStore}',
                    hidden: '{!isPagingToolBarVisible}'
                },
                hidden: true,
                dock: 'top'
            }],
            listeners: {
                itemclick: 'onClusterItemClick'
            }
        }
    ]
});

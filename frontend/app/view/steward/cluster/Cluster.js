/**
 * Экран кластеров
 *
 * @author Ivan Marshalkin
 * @date 2016-10-26
 */

Ext.define('Unidata.view.steward.cluster.Cluster', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.steward.cluster.ClusterController',
        'Unidata.view.steward.cluster.ClusterModel',

        'Unidata.view.steward.cluster.filter.ClusterFilter',
        'Unidata.view.steward.cluster.list.ClusterList',
        'Unidata.view.steward.cluster.tabpanel.ClusterTabPanel'
    ],

    alias: 'widget.steward.cluster',

    viewModel: {
        type: 'steward.cluster'
    },

    controller: 'steward.cluster',

    cls: 'un-section-cluster',

    eventBusHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    referenceHolder: true,

    clusterFilterPanel: null,                                        // панель фильтрации
    clusterListPanel: null,                                          // панель результатов поиска
    clusterTabPanel: null,                                           // панель просмотра содержимого кластеров

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [],

    config: {},

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.clusterFilterPanel = this.lookupReference('clusterFilterPanel');
        me.clusterListPanel   = this.lookupReference('clusterListPanel');
        me.clusterTabPanel    = this.lookupReference('clusterTabPanel');
    },

    onDestroy: function () {
        var me = this;

        me.clusterFilterPanel = null;
        me.clusterListPanel   = null;
        me.clusterTabPanel    = null;

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'steward.cluster.filterview',
            reference: 'clusterFilterPanel',
            collapsible: 'true',
            collapseDirection: 'left',
            animCollapse: false,
            titleCollapse: true,
            width: 300
        },
        {
            xtype: 'steward.cluster.listview',
            reference: 'clusterListPanel',
            collapsible: 'true',
            collapseDirection: 'left',
            animCollapse: false,
            titleCollapse: true,
            width: 300
        },
        {
            xtype: 'steward.cluster.tabpanelview',
            reference: 'clusterTabPanel',
            flex: 1,
            listeners: {
                datarecordsavesuccess: 'onDataRecordSaveSuccess',
                datarecorddeletesuccess: 'onDataRecordDeleteSuccess'
            }
        }
    ]
});

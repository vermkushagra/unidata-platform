/**
 * Панель вкладок для экрана кластеров
 *
 * @author Ivan Marshalkin
 * @date 2016-10-26
 */

Ext.define('Unidata.view.steward.cluster.tabpanel.ClusterTabPanel', {
    extend: 'Ext.tab.Panel',

    requires: [
        'Unidata.view.steward.cluster.tabpanel.ClusterTabPanelController',
        'Unidata.view.steward.cluster.tabpanel.ClusterTabPanelModel',

        'Unidata.view.steward.cluster.merge.Merge'
    ],

    alias: 'widget.steward.cluster.tabpanelview',

    viewModel: {
        type: 'steward.cluster.tabpanelview'
    },

    controller: 'steward.cluster.tabpanelview',

    referenceHolder: true,

    ui: 'un-content',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    mixins: [
        'Unidata.mixin.StatusManageable'
    ],

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'openClusterTab'
        }
    ],

    config: {},

    initComponent: function () {
        this.callParent(arguments);
    },

    items: [
    ]
});

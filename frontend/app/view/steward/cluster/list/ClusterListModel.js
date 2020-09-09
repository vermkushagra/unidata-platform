/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-10-26
 */

Ext.define('Unidata.view.steward.cluster.list.ClusterListModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.cluster.listview',

    data: {
        totalCount: 0,
        pageSize: 0,
        preprocessing: false
    },

    stores: {
        clusterListStore: {
            model: 'Unidata.model.cluster.ClusterSearchHit',
            autoLoad: false,
            pageSize: Unidata.Config.getCustomerCfg()['SEARCH_ROWS'],
            proxy: {
                type: 'un.clusterlist'
            },
            listeners: {
                beforeload: 'onClusterListStoreBeforeLoad'
            }
        }
    },

    formulas: {
        /**
         * Определяет видимость пейджинга
         */
        isPagingToolBarVisible: {
            bind: {
                preprocessing: '{preprocessing}',
                totalCount: '{totalCount}',
                pageSize: '{pageSize}'
            },

            get: function (getter) {
                var visible = false;

                if (getter.totalCount > getter.pageSize) {
                    visible = true;
                }

                return Ext.coalesceDefined(visible, false);
            }
        }
    }
});

/**
 * Таблица списков записей
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.etaloncluster.component.EtalonClusterGrid', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.component.search.query.etaloncluster.component.etalonclustergrid',

    mixins: [
        'Unidata.view.component.search.query.relation.mixin.EtalonClusterMixin'
    ],

    config: {
        entityName: null
    },

    store: {
        // model stored in property records from Unidata.module.EtalonCluster
        model: 'Unidata.model.etaloncluster.EtalonCluster',
        proxy: {
            //enablePaging: true,
            type: 'memory'
        }
    },

    columns: [
        {
            dataIndex: 'name',
            flex: 1
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        this.initStore();
    }
});

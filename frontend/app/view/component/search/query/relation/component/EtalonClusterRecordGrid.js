/**
 * Таблица записей из массива записей
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.relation.component.EtalonClusterRecordGrid', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.component.search.query.relation.component.etalonrecordgrid',

    config: {
        etalonCluster: null
    },

    store: {
        // model stored in property records from Unidata.module.EtalonCluster
        model: 'Unidata.model.etaloncluster.EtalonClusterRecord',
        proxy: {
            //enablePaging: true,
            type: 'memory'
        }
    },

    selModel: {
        selType: 'checkboxmodel',
        checkOnly: true,
        pruneRemoved: false,
        injectCheckbox: 'first'
    },

    columns: [
        {
            dataIndex: 'displayValue',
            flex: 1
        }
    ],

    updateEtalonCluster: function (etalonCluster) {
        this.setStoreData(etalonCluster);
    },

    /**
     *
     * @param etalonCluster {Unidata.model.etaloncluster.EtalonCluster}
     */
    setStoreData: function (etalonCluster) {
        var recordsRange = [],
            store;

        if (etalonCluster) {
            recordsRange = etalonCluster.records().getRange();
        }
        store = this.getStore();
        store.getProxy().setData(recordsRange);
        store.load();
    }
});

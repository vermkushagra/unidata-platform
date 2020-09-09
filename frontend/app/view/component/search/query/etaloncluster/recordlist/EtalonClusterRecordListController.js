Ext.define('Unidata.view.component.search.query.etaloncluster.recordlist.EtalonClusterRecordListController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.query.etaloncluster.recordlist.etalonclusterrecordlist',

    onEtalonClusterRecordGridSelectionChange: function (self, selected) {
        var view = this.getView();

        view.setSelectedCount(selected.length);
    },

    deleteEtalonClusterRecord: function () {
        var EtalonClusterStorage = Unidata.module.storage.EtalonClusterStorage,
            view = this.getView(),
            etalonCluster = view.getEtalonCluster(),
            etalonClusterRecordGrid = view.etalonClusterRecordGrid,
            store = etalonClusterRecordGrid.getStore(),
            etalonClusterRecords;

        etalonClusterRecords = etalonClusterRecordGrid.getSelection();
        store.remove(etalonClusterRecords);
        EtalonClusterStorage.removeEtalonClusterRecords(etalonCluster, etalonClusterRecords);
    }
});

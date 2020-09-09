Ext.define('Unidata.view.component.search.query.etaloncluster.list.EtalonClusterListController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.query.etaloncluster.list.etalonclusterlist',

    /**
     *
     * @param self {Unidata.view.component.search.query.etaloncluster.component.EtalonClusterGrid}
     * @param selected {Unidata.model.etaloncluster.EtalonCluster[]}
     */
    onEtalonClusterGridSelectionChange: function (self, selected) {
        var view = this.getView(),
            etalonCluster;

        etalonCluster = selected.length > 0 ? selected[0] : null;

        view.setEtalonCluster(etalonCluster);

        view.fireEvent('etalonclustergridselectionchange', etalonCluster);
    },

    onEntitySelect: function (combobox, entity) {
        var view = this.getView(),
            etalonClusterGrid;

        if (!entity) {
            return;
        }

        etalonClusterGrid = view.etalonClusterGrid;
        etalonClusterGrid.setEntityName(entity.get('name'));
    },

    deleteEtalonCluster: function () {
        var EtalonClusterStorage = Unidata.module.storage.EtalonClusterStorage,
            view = this.getView(),
            etalonClusterGrid = view.etalonClusterGrid,
            store = etalonClusterGrid.getStore(),
            etalonClusters;

        etalonClusters = etalonClusterGrid.getSelection();
        store.remove(etalonClusters);
        EtalonClusterStorage.removeEtalonClusters(etalonClusters);
    }
});

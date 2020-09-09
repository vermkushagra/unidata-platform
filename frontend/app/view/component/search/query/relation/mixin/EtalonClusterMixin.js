/**
 * Миксин для работы со списками записей для разнообразных компонентов
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.relation.mixin.EtalonClusterMixin', {
    extend: 'Ext.Mixin',

    initStore: function () {
        var EtalonClusterStorage = Unidata.module.storage.EtalonClusterStorage,
            store = this.getStore(),
            etalonClusters;

        store.setRemoteFilter(false);

        etalonClusters = EtalonClusterStorage.getEtalonClusters();

        if (etalonClusters.length === 0) {
            return;
        }
        store.getProxy().setData(etalonClusters);
        store.load();
    },

    updateEntityName: function (entityName) {
        var selection = this.getSelection(),
            store = this.getStore();

        this.filterEtalonClusters(entityName);

        if (selection.length > 0 && store.indexOf(selection[0]) === -1) {
            this.setSelection(null);
        }
    },

    filterEtalonClusters: function (entityName) {
        var store = this.getStore(),
            filters = [];

        filters.push(new Ext.util.Filter({
            property: 'entityName',
            value: entityName
        }));

        filters.push(new Ext.util.Filter({
            property: 'entityType',
            value: 'Entity'
        }));

        store.addFilter(filters);
    }
});

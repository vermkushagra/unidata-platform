/**
 * API взаимодействия с enumeration
 *
 * @author Ivan Marshalkin
 * @date 2016-08-09
 */

Ext.define('Unidata.util.api.Enumeration', {
    extend: 'Unidata.util.api.AbstractApi',
    singleton: true,

    getEnumerations: function () {
        var deferred = Ext.create('Ext.Deferred'),
            store = this.getStore();

        this.loadStore(store)
            .then(
                function (store) {
                    deferred.resolve(
                        store.getRange()
                    );
                },
                function () {
                    deferred.reject();
                }
            )
            .done();

        return deferred.promise;
    },

    /**
     * Возвращает глобальный стор с enumerations
     * @returns {Ext.data.Store}
     */
    getStore: function () {
        var me = this,
            store = Ext.data.StoreManager.lookup('enumerations');

        if (!store) {
            store = Ext.create('Unidata.store.EnumerationStore', {
                storeId: 'enumerations',
                proxy: {
                    reader: {
                        transform: function (data) {
                            return me.sortEnumerationData(data);
                        }
                    }
                }
            });
        }

        return store;
    },

    sortEnumerationData: function (data) {
        if (!Ext.isArray(data) || !data.length) {
            // необходимо возвращать значение иначе reader падает при обращении к undefined
            return [];
        }

        Ext.Array.each(data, function (dataItem) {
            this.sortEnumerationData(dataItem.values);
        }, this);

        data = Ext.Array.sort(data, function (a, b) {
            if (a.displayName === b.displayName) {
                return 0;
            }

            if (a.displayName > b.displayName) {
                return 1;
            } else {
                return -1;
            }
        });

        return data;
    },

    getStoreLoaded: function (forceReload, loadCfg) {
        var store = this.getStore();

        return this.loadStore(store, forceReload, loadCfg);
    },

    getStoreReloaded: function (loadCfg) {
        var store = this.getStore();

        return this.reloadStore(store, loadCfg);
    }
});

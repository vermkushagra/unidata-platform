/**
 * Базовый класс API взаимодействия
 *
 * @author Sergey Shishigin
 * @date 2016-10-20
 */

Ext.define('Unidata.util.api.AbstractApi', {

    /**
     * Возвращает промис со стором, который уже загружен
     * @returns {Ext.promise.Promise}
     */
    loadStore: function (store, forceReload, loadCfg) {
        var deferred = Ext.create('Ext.Deferred');

        loadCfg = loadCfg || {};

        forceReload = forceReload !== undefined ? forceReload : false;

        if (store.isLoaded() && !store.isLoading() && !forceReload) {

            deferred.resolve(store);

        } else {
            if (store.isLoading()) {

                store.on('load', function (store, records, successful) {
                    if (successful) {
                        deferred.resolve(store);
                    } else {
                        deferred.reject();
                    }
                }, this, {single: true});

            } else {

                loadCfg = Ext.apply({
                    callback: function (records, operation, success) {
                        if (success) {
                            deferred.resolve(store);
                        } else {
                            deferred.reject();
                        }
                    }
                }, loadCfg);

                store.load(loadCfg);

            }
        }

        return deferred.promise;
    },

    /**
     * Возвращает промис со стором, который уже загружен (с принудительной перезагрузкой)
     * @returns {Ext.promise.Promise}
     */
    reloadStore: function (store, loadCfg) {
        var promise;

        promise = this.loadStore(store, true, loadCfg);

        return promise;
    }
});

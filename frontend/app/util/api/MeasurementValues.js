/**
 * API взаимодействия с величинами единиц измерения
 *
 * @author Ivan Marshalkin
 * @date 2016-11-09
 */

Ext.define('Unidata.util.api.MeasurementValues', {
    extend: 'Unidata.util.api.AbstractApi',
    singleton: true,

    getMeasurementValues: function () {
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

    getMeasurementValueById: function (id) {
        var measurementValue = null,
            store = this.getStore();

        measurementValue = store.getById(id);

        return measurementValue;
    },

    getMeasurementUnit: function (valueId, unitId) {
        var measurementValue = this.getMeasurementValueById(valueId),
            measurementUnits;

        if (!measurementValue) {
            return null;
        }

        measurementUnits = measurementValue.measurementUnits();

        return measurementUnits.findRecord('id', unitId, 0, false, false, true);
    },

    /**
     * Возвращает глобальный стор с величинами единиц измерения
     *
     * @returns {Ext.data.Store}
     */
    getStore: function () {
        var store = Ext.data.StoreManager.lookup('measurementValues');

        if (!store) {
            store = Ext.create('Unidata.store.MeasurementValuesStore', {
                storeId: 'measurementValues'
            });
        }

        return store;
    },

    getStoreLoaded: function (forceReload, loadCfg) {
        var store = this.getStore();

        return this.loadStore(store, forceReload, loadCfg);
    },

    getStoreReloaded: function (loadCfg) {
        var store = this.getStore();

        return this.reloadStore(store, loadCfg);
    },

    /**
     * Удаляет измеряемую величину
     *
     * @param valueId
     * @returns {null|Ext.promise|Ext.promise.Promise|*}
     */
    removeMeasurementValue: function (valueId) {
        var deferred = Ext.create('Ext.Deferred'),
            url      = Unidata.Api.getMeasurementValueDeleteUrl(valueId);

        Ext.Ajax.request({
            url: url,
            method: 'DELETE',
            success: function () {
                deferred.resolve();
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    },

    /**
     * Удаляет измеряемые величины
     *
     * @param ids - идентификаторы для удаления
     * @param draft - флаг режима работы с черновиком (true | false)
     *
     * @returns {null|Ext.promise|Ext.promise.Promise|*}
     */
    removeMeasurementValues: function (ids, draft) {
        var deferred = Ext.create('Ext.Deferred'),
            url      = Unidata.Api.getMeasurementValueBatchDeleteUrl();

        Ext.Ajax.request({
            url: url,
            method: 'GET',
            params: {
                valueId: ids,
                draft: draft
            },
            success: function (response) {
                var responseJson;

                responseJson = JSON.parse(response.responseText);

                deferred.resolve(responseJson.success);
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    }
});

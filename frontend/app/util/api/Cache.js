/**
 * API для загрузки данных необходимых для работы (системный кэш)
 *
 * @author Sergey Shishigin
 * @date 2016-11-01
 */

Ext.define('Unidata.util.api.Cache', {
    extend: 'Unidata.util.api.AbstractApi',
    singleton: true,

    /**
     * Загрузка всех данных, которые необходимы для работы
     *
     * @returns {Ext.promise.Promise}
     */
    loadCache: function () {
        var MetaRecordUtil     = Unidata.util.MetaRecord,
            enumPromise,
            measurementPromise,
            entitiesPromise,
            lookupEntitiesPromise,
            enumerationLoadCfg,
            measurementValuesLoadCfg;

        enumerationLoadCfg = measurementValuesLoadCfg = {
            params: {
                draft: false
            }
        };

        // первично загружаем данные
        enumPromise        = Unidata.util.api.Enumeration.getStoreLoaded(true, enumerationLoadCfg);
        measurementPromise = Unidata.util.api.MeasurementValues.getStoreLoaded(true, measurementValuesLoadCfg);
        // entities и lookupEntities не грузятся именно в кэш, но необходимы для выполнения очистки localStorage
        // закладка на будущее, когда они будут в кэше
        entitiesPromise = Unidata.util.api.MetaRecord.getMetaRecords({entityType: MetaRecordUtil.TYPE_ENTITY});
        lookupEntitiesPromise = Unidata.util.api.MetaRecord.getMetaRecords({entityType: MetaRecordUtil.TYPE_LOOKUP});

        return Ext.Deferred.all([enumPromise, measurementPromise, entitiesPromise, lookupEntitiesPromise]);
    }
});

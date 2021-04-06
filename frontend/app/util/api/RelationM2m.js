/**
 * API для связей типа многие-ко-многим
 *
 * @author Ivan Marshalkin
 * @date 2016-05-14
 */

Ext.define('Unidata.util.api.RelationM2m', {

    singleton: true,

    /**
     * Получает мета записи зависимостей типа включение
     *
     * @param metaRecord
     * @returns {*}
     */
    getMetaRelations: function (metaRecord) {
        return metaRecord.relations().query('relType', 'ManyToMany');
    },

    /**
     * Получает все записи-связи типа многие-ко-многим по метамодели
     *
     * @param metaRecord - модель записи
     * @param etalonId   - идентификатор записи для которой возвратить связи
     * @param dateFrom   - дата начала интервала для которого ищем связи
     * @param dateTo     - дата конца интервала для которого ищем связи
     *
     * @returns {*|Ext.promise.Promise|Ext.Promise}
     */
    getRelations: function (metaRecord, etalonId, dateFrom, dateTo) {
        var me         = this,
            relations  = metaRecord.relations().query('relType', 'ManyToMany'),
            promises   = [];

        // грузим все связи
        relations.each(function (metaRelation) {
            var promise;

            promise = me.getRelation(metaRelation.get('name'), etalonId, dateFrom, dateTo)
                .then(function (dataRelation) {
                    var result;

                    result = {
                        metaRelation: metaRelation,
                        dataRelation: dataRelation
                    };

                    return result;
                });

            promise.done();

            promises.push(promise);
        });

        // когда все связи прогрузятся, ресолвим или реджектим
        return Ext.Deferred.all(promises);
    },

    /**
     * Получает записи-связи типа многие-ко-многим для конкретной связи
     *
     * @param relationName
     * @param etalonId
     * @param dateFrom
     * @param dateTo
     * @param drafts
     * @param operationId
     * @returns {*|Promise|Ext.promise.Promise}
     */
    getRelation: function (relationName, etalonId, dateFrom, dateTo, drafts, operationId) {
        var me       = this,
            promise;

        // загружаем таймлайн
        promise = me
            .loadRelationTimeline(relationName, etalonId, dateFrom, dateTo, drafts, operationId)
            // успешно загрузили таймлайн для связи
            .then(function (records) {
                var promise;

                promise = me.loadRelationRecords(records, drafts, operationId);

                return promise;
            });

        return promise;
    },

    /**
     * Получает _ВСЕ_ записи-связи типа многие-ко-многим для конкретной связи одним запросом
     *
     * @param relationName
     * @param etalonId
     * @param dateFrom
     * @param dateTo
     * @param drafts
     * @param operationId
     * @returns {*|Promise|Ext.promise.Promise}
     */
    getRelationBulk: function (relationName, etalonId, dateFrom, dateTo, drafts, operationId) {
        var deferred = Ext.create('Ext.Deferred'),
            dateFormat = Unidata.Config.getDateTimeFormatProxy(),
            extraParams,
            url,
            store;

        extraParams = {
            drafts: drafts
        };

        if (operationId) {
            extraParams.operationId = operationId;
        }

        dateFrom = Ext.Date.format(dateFrom, dateFormat) || 'null';
        dateTo   = Ext.Date.format(dateTo,   dateFormat) || 'null';

        url = Unidata.Config.getMainUrl() + 'internal/data/relations/relation_bulk/' +
            etalonId + '/' + relationName +
            '/' + dateFrom + '/' + dateTo;

        Ext.Ajax.request({
            url: url,
            method: 'GET',
            success: function (response) {
                var jsonResp = Ext.util.JSON.decode(response.responseText, true),
                    reader,
                    records;

                if (response.status === 200 && jsonResp) {
                    // создать записи и зарезолвить массив records
                    reader = Ext.create('Ext.data.JsonReader', {
                        model: 'Unidata.model.data.RelationsTo',
                        rootProperty: 'content'
                    });

                    records = reader.readRecords(jsonResp);
                    records = records.records;

                    deferred.resolve(records);
                } else {
                    deferred.reject();
                }
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    },

    /**
     * Загружает таймлайн конкретной связи
     *
     * @param relationName
     * @param etalonId
     * @param dateFrom
     * @param dateTo
     * @param drafts
     * @param operationId
     * @returns {null|Ext.promise|*|Ext.promise.Promise}
     */
    loadRelationTimeline: function (relationName, etalonId, dateFrom, dateTo, drafts, operationId) {
        var deferred     = Ext.create('Ext.Deferred'),
            dateFormat   = Unidata.Config.getDateTimeFormatProxy(),
            extraParams = {
                drafts: drafts
            },
            url,
            store;

        if (operationId) {
            extraParams.operationId = operationId;
        }

        dateFrom = Ext.Date.format(dateFrom, dateFormat) || 'null';
        dateTo   = Ext.Date.format(dateTo,   dateFormat) || 'null';

        url = Unidata.Config.getMainUrl() + 'internal/data/relations/timeline/' +
            etalonId + '/' + relationName +
            '/' + dateFrom + '/' + dateTo;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.data.Relation',
            proxy: {
                type: 'data.relationproxy',
                url: url,
                extraParams: extraParams,
                reader: {
                    rootProperty: 'content'
                }
            }
        });

        // подгружаем информацию о связи
        store.on('load', function (store, records, successful) {

            if (!successful) {
                deferred.reject();
            } else {
                // удаляем записи из стора чтоб его смог собрать GC
                store.removeAll(true);

                deferred.resolve(records);
            }

        });

        store.load();

        return deferred.promise;
    },

    /**
     * Загружает связи по записям из таймлайна конкретной связи
     *
     * @param timelineRecords
     * @param drafts
     * @returns {*|Ext.promise.Promise|Ext.Promise}
     */
    loadRelationRecords: function (timelineRecords, drafts, operationId) {
        var me       = this,
            promises = [];

        // получаем собственно связь
        Ext.Array.each(timelineRecords, function (record) {
            var promise,
                collection,
                timelineRecord;

            // загружаем только активные связи
            collection = record.timeline().query('active', true);

            if (!collection.getCount()) {
                return;
            }

            // берем первый активный элемент из коллекции
            timelineRecord = collection.getAt(0);

            promise = me.loadRelationRecord(
                record.get('etalonId'),
                timelineRecord.get('dateFrom'),
                timelineRecord.get('dateTo'),
                drafts,
                operationId
            );

            promises.push(promise);
        });

        return Ext.Deferred.all(promises);
    },

    /**
     * Загружает связь
     *
     * @param etalonId - идентификатор записи-связи
     * @param dateFrom - дата начала действия записи-связи
     * @param dateTo   - дата конец действия записи-связи
     * @param drafts
     * @param operationId
     *
     * @returns {null|Ext.promise|Ext.promise.Promise|*}
     */
    loadRelationRecord: function (etalonId, dateFrom, dateTo, drafts, operationId) {
        var deferred   = Ext.create('Ext.Deferred'),
            relationTo = Ext.create('Unidata.model.data.RelationsTo'),
            proxy      = relationTo.getProxy();

        proxy.dateFrom = dateFrom || dateTo;
        proxy.etalonId = etalonId;

        proxy.setExtraParam('drafts', drafts);

        if (operationId) {
            proxy.setExtraParam('operationId', operationId);
        } else {
            delete proxy.extraParams.operationId;
        }

        // загружаем связь
        relationTo.load({
            success: function (relation) {
                deferred.resolve(relation);
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    }
});

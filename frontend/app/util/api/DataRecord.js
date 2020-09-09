/**
 * API взаимодействия с DataRecord
 */

Ext.define('Unidata.util.api.DataRecord', {
    singleton: true,

    getDataRecord: function (cfg) {
        var deferred,
            date           = cfg.date,
            etalonId       = cfg.etalonId,
            intervalActive = cfg.intervalActive,
            drafts         = cfg.drafts,
            diffToDraft         = cfg.diffToDraft,
            operationId    = cfg.operationId,
            url            = cfg.url || Unidata.Config.getMainUrl() + 'internal/data/entities/',
            record,
            recordProxy,
            oldUrl         = url;

        deferred = new Ext.Deferred();

        record = Ext.create('Unidata.model.data.Record');
        recordProxy = record.getProxy();

        recordProxy.setUrl(url);

        record.setId(etalonId);
        recordProxy.setDate(date);

        recordProxy.setExtraParam('drafts', Boolean(drafts));
        recordProxy.setExtraParam('diffToDraft', Boolean(diffToDraft));

        if (operationId) {
            recordProxy.setExtraParam('operationId', operationId);
        } else {
            delete recordProxy.extraParams.operationId;
        }

        if (Ext.isBoolean(intervalActive)) {
            recordProxy.setExtraParam('inactive', !intervalActive);
        }

        record.load({
            success: function (record) {
                Unidata.util.DataRecord.bindManyToOneAssociationListeners(record);
                deferred.resolve(record);
            },
            failure: function () {
                deferred.reject();
            }
        });

        // restore url
        record.getProxy().setUrl(oldUrl);
        record.getProxy().setDate(null);

        return deferred.promise;
    },

    /**
     * Загрузка data records по массиву etalonIds и dates
     *
     * @param dataRecordKeys Массив структур {etalonId: string, date: string} или Unidata.model.data.DataRecordKey
     */
    getDataRecordsByKeys: function (dataRecordKeys) {
        var promises,
            me = this;

        // строим массив промисов на базе массива cfg
        promises = dataRecordKeys.map(function (cfg) {
            var promise;

            if (Ext.isString(cfg)) {
                cfg = {
                    etalonId: cfg,
                    date: null
                };
            } else if (cfg instanceof Unidata.model.data.DataRecordKey) {
                cfg = cfg.getData();
            }

            promise = me.getDataRecord(cfg);
            promise.done();

            return promise;
        });

        return Ext.Deferred.all(promises);
    },

    preRestoreValidateDataRecord: function (cfg) {
        var url,
            mainUrl = Unidata.Config.getMainUrl(),
            tplUrl = '{0}internal/data/entities/pre-restore-validation/{1}/{2}',
            etalonId = cfg.etalonId,
            date = cfg.date,
            dateFormat = Unidata.Config.getDateTimeFormatProxy(),
            deferred;

        date = Ext.Date.format(date, dateFormat);
        url = Ext.String.format(tplUrl, mainUrl, etalonId, date);
        deferred = new Ext.Deferred();

        Ext.Ajax.unidataRequest({
            url: url,
            method: 'GET',
            success: function () {
                var inconsistentFields = null;

                deferred.resolve(inconsistentFields);
            },
            failure: function (response) {
                var inconsistentFields = null,
                    ErrorMessageFactoryUtil = Unidata.util.ErrorMessageFactory,
                    error,
                    errorCode = 'EX_DATA_CANNOT_DELETE_REF_EXIST';

                error = ErrorMessageFactoryUtil.getErrorFromResponseByErrorCode(response, errorCode);

                if (error && error.params) {
                    inconsistentFields = Ext.Array.pluck(error.params, 'key');
                }
                deferred.resolve(inconsistentFields);
            }
        });

        return deferred.promise;
    },

    publishJms: function (cfg) {
        var mainUrl = Unidata.Config.getMainUrl(),
            tplUrl = '{0}internal/notifications/send/{1}/{2}',
            etalonId = cfg.etalonId,
            date = cfg.date,
            dateFormat = Unidata.Config.getDateTimeFormatProxy(),
            url,
            deferred;

        deferred = new Ext.Deferred();
        date = Ext.Date.format(date, dateFormat);
        url = Ext.String.format(tplUrl, mainUrl, etalonId, date);

        Ext.Ajax.unidataRequest({
            url: url,
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            params: {
                drafts: false,
                inactive: false
            },
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
     * Атомарный upsert
     *
     * @param {Object} data
     * @return {*}
     */
    saveAtomic: function (etalonId, data, create) {
        var me = this,
            deferred,
            atomicRecord,
            method,
            url;

        create = Ext.isBoolean(create) ? create : false;
        url = Unidata.Api.getAtomicUpsertUrl(etalonId);
        deferred = Ext.create('Ext.Deferred');
        method = create ? 'POST' : 'PUT';

        Ext.Ajax.unidataRequest({
            method: method,
            url: url,
            jsonData: Ext.util.JSON.encode(data),
            success: function (response) {
                var atomicRecord;

                atomicRecord = me.parseDataRecord(response);
                deferred.resolve(atomicRecord);
            },
            failure: function (response) {
                var atomicRecord;

                atomicRecord = me.parseDataRecord(response);

                deferred.reject(atomicRecord);
            }
        });

        return deferred.promise;
    },

    /**
     * Получить dataRecord из ответа
     *
     * @param {String} response
     * @return {Unidata.model.data.Record}
     */
    parseDataRecord: function (response) {
        var responseJson,
            dataRecord = null,
            reader,
            data,
            resultSet;

        responseJson = Ext.util.JSON.decode(response.responseText, true);

        if (responseJson.content) {
            data = responseJson.content;

            reader = Ext.create('Ext.data.JsonReader', {
                model: 'Unidata.model.data.Record'
            });

            resultSet = reader.readRecords(data);

            if (resultSet && resultSet.count > 0) {
                dataRecord = resultSet.records[0];
            }
        }

        return dataRecord;
    },

    /**
     * Построение atomic record for atomic upsert
     *
     * @return {Unidata.model.data.AtomicRecord}
     */
    buildAtomicRecord: function (cfg) {
        var dataRecord         = cfg.dataRecord,
            phantom = cfg.phantom,
            relationReferenceDiff  = cfg.relationReferenceDiff,
            relationContainsDiff   = cfg.relationContainsDiff,
            relationManyToManyDiff = cfg.relationManyToManyDiff,
            atomicRecord;

        atomicRecord = Ext.create('Unidata.model.data.AtomicRecord');
        atomicRecord.phantom = phantom;

        if (dataRecord) {
            atomicRecord.setDataRecord(dataRecord);
        }

        if (relationReferenceDiff) {
            atomicRecord.setRelationReference(relationReferenceDiff);
        }

        if (relationContainsDiff) {
            atomicRecord.setRelationContains(relationContainsDiff);
        }

        if (relationManyToManyDiff) {
            atomicRecord.setRelationManyToMany(relationManyToManyDiff);
        }

        return atomicRecord;
    },

    /**
     * Определяет доступные etalonId для политики пересечения временных интервалов
     *
     * @param etalonIds - массив etalonId проверяемых записей
     * @param validFrom - левая граница временного интервала
     * @param validTo - правая граница временного интервала
     * @param timeIntervalIntersectType - политика пересечения временных интервалов
     *
     * @returns {*}
     */
    allowedDataRecordByTimeIntervalIntersectType: function (etalonIds, validFrom, validTo, timeIntervalIntersectType) {
        var url = Unidata.Api.createUrl('/data/entities/filter-by-criteria'),
            dateTimeFormatProxy = Unidata.Config.getDateTimeFormatProxy(),
            data,
            deferred;

        deferred = Ext.create('Ext.Deferred');

        data = {
            etalonIds: etalonIds,
            validFrom: validFrom ? Ext.Date.format(validFrom, dateTimeFormatProxy) : null,
            validTo: validTo ? Ext.Date.format(validTo, dateTimeFormatProxy) : null,
            timeIntervalIntersectType: timeIntervalIntersectType
        };

        Ext.Ajax.unidataRequest({
            method: 'POST',
            url: url,
            jsonData: Ext.util.JSON.encode(data),
            success: function (response) {
                var responseJson;

                responseJson = Ext.util.JSON.decode(response.responseText, true);

                if (responseJson.success === true && responseJson.content) {
                    deferred.resolve(responseJson.content);
                } else {
                    deferred.reject();
                }
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    }
});

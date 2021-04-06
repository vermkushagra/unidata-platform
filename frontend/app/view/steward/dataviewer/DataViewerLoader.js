/**
 * Загрузчик данных для DataViewer
 *
 * author: Sergey Shishigin
 */

Ext.define('Unidata.view.steward.dataviewer.DataViewerLoader', {
    singleton: true,

    requires: [
        'Unidata.util.api.RelationReference'
    ],

    loadOrigin: function (etalonId, promiseTimeline, timeIntervalStore, timeIntervalDate) {
        var promiseFindOriginTimeInterval,
            promiseGetOriginRecords,
            me = this;

        function onGetOriginTimelineFulfilled (store) {
            return Unidata.view.component.timeinterval.TimeIntervalDataView.findTimeInterval(store, timeIntervalDate);
        }

        function onGetOriginTimelineRejected () {
            throw new Error(Unidata.i18n.t('validation:loadFailed', {name: Unidata.i18n.t('glossary:timeintervals')}));
        }

        function onFindOriginTimeIntervalFulfilled (timeInterval) {
            var date;

            if (!timeInterval) {
                throw new Error(Unidata.i18n.t('dataviewer>timeIntervalError'));
            }

            date = Unidata.view.component.timeinterval.TimeIntervalDataView.getTimeIntervalDate(timeInterval);

            return me.loadOriginRecords({
                etalonId: etalonId,
                date: date
            });
        }

        function onFindOriginTimeIntervalRejected () {
            throw new Error(Unidata.i18n.t('dataviewer>findOriginTimeIntervalError'));
        }

        // calculate an appropriate time interval
        promiseFindOriginTimeInterval = promiseTimeline.then(onGetOriginTimelineFulfilled, onGetOriginTimelineRejected);
        promiseFindOriginTimeInterval.done();

        //TODO: implement me
        promiseGetOriginRecords = promiseFindOriginTimeInterval.then(onFindOriginTimeIntervalFulfilled, onFindOriginTimeIntervalRejected);  // jscs:ignore maximumLineLength
        promiseGetOriginRecords.done();

        return promiseGetOriginRecords;
    },

    loadBackRels: function (promiseGetMetaRecord, etalonId) {
        var promiseGetRelationsDigest,
            me = this;

        function onGetMetaRecordFulfilled (metaRecord) {
            return me.loadBackRelations({
                metaRecord: metaRecord,
                etalonId: etalonId,
                dateFrom: null,
                dateTo: null,
                relDirection: 'from'
            });
        }

        function onGetMetaRecordRejected () {
            throw new Error(Unidata.i18n.t('validation:loadFailed', {name: Unidata.i18n.t('glossary:metamodel')}));
        }

        promiseGetRelationsDigest = promiseGetMetaRecord.then(onGetMetaRecordFulfilled, onGetMetaRecordRejected);
        promiseGetRelationsDigest.done();

        return promiseGetRelationsDigest;
    },

    loadClusterCount: function (promiseGetMetaRecord, etalonId) {
        var promiseClusterCount;

        function onGetMetaRecordFulfilled (metaRecord) {
            var entityName = metaRecord.get('name');

            return Unidata.util.api.Cluster.getClusterCount(entityName, null, null, etalonId);
        }

        function onGetMetaRecordRejected () {
            throw new Error(Unidata.i18n.t('validation:loadFailed', {name: Unidata.i18n.t('glossary:metamodel')}));
        }

        promiseClusterCount = promiseGetMetaRecord.then(onGetMetaRecordFulfilled, onGetMetaRecordRejected);
        promiseClusterCount.done();

        return promiseClusterCount;
    },

    /**
     * Загрузка данных на основе имеющихся данных из newConfig, newOptions
     * Возвращает промис
     *
     * @param newConfig Конфиг предопределенных параметров
     * @param newOptions Опции
     *
     * Поля, указанные в newConfig, newOptions переопределяют соотв. поля config, options
     * Поля newConfig принимают значение null, если не указаны
     * Поля newOptions принимают значения по умолчанию, если не указаны
     *
     * Поля config/newConfig:
     * - dataRecord
     * - etalonId
     * - metaRecord
     * - timeIntervalDate
     * - originTimeIntervalDate
     * - timeIntervalStore
     *
     * Поля options/newOptions (в сбоках - значения по умолчанию):
     * isReloadTimeline (true)
     * isLoadBackRels (false)
     * isLoadOriginRecords (false)
     * isLoadClusterCount (false)
     *
     * @returns {*|Ext.promise.Promise|Promise}
     */
    load: function (newConfig, newOptions) {
        var promises = [],
            promiseTimeline,
            promiseFindTimeInterval,
            promiseGetDataRecord,
            promiseGetMetaRecord,
            promiseReferenceRelations,
            promiseRelationsDigest,
            promiseOriginRecords,
            promiseGetClassifierNode,
            promiseClusterCount,
            promiseAll,
            promiseResult,
            deferred,
            me = this,
            config,
            options;

        newOptions = newOptions || {};

        config = {
            dataRecord: null,
            etalonId: null,
            metaRecord: null,
            timeIntervalDate: null,
            originTimeIntervalDate: null,
            timeIntervalStore: null
        };

        options = {
            isReloadTimeline: true,
            isLoadBackRels: false,
            isLoadOriginRecords: false,
            isLoadClusterCount: true,
            isLoadReferenceRelations: true
        };

        Ext.Object.merge(config, newConfig);
        Ext.Object.merge(options, newOptions);

        function onGetTimelineFulfilled (store) {
            return Unidata.view.component.timeinterval.TimeIntervalDataView.findTimeInterval(store, config.timeIntervalDate);
        }

        function onGetTimelineRejected () {
            throw new Error(Unidata.i18n.t('dataviewer>loadTimeItervalError'));
        }

        function onFindTimeIntervalFulfilled (timeInterval) {
            if (!timeInterval) {
                throw new Error(Unidata.i18n.t('dataviewer>timeIntervalError'));
            }

            return me.loadDataRecord(newConfig, timeInterval, config.dataRecord, config.etalonId);
        }

        function onFindTimeIntervalRejected () {
            throw new Error(Unidata.i18n.t('validation:loadFailed', {name: Unidata.i18n.t('glossary:dataRecord')}));
        }

        function onGetDataRecordFulfilled (dataRecord) {
            return me.loadMetaRecord(newConfig, dataRecord, config.metaRecord);
        }

        function onGetDataRecordRejected () {
            throw new Error(Unidata.i18n.t('dataviewer>loadDataRecordError'));
        }

        function onGetClassifierNodesFulfilled (classifierNodes) {
            newConfig.classifierNodes = classifierNodes;
        }

        function onGetClassifierNodesRejected () {
            throw new Error(Unidata.i18n.t('dataviewer>loadClassifierNodesInfoError'));
        }

        function onGetMetaRecordFulfilled (metaRecord) {
            return me.loadReferenceRelations(newConfig, metaRecord, config.etalonId);
        }

        function onGetMetaRecordRejected () {
            throw new Error(Unidata.i18n.t('validation:loadFailed', {name: Unidata.i18n.t('glossary:metamodel')}));
        }

        if (!config.dataRecord && !config.etalonId) {
            throw 'etalonId or dataRecord are required';
        }

        // заполняем etalonId, если знаем dataRecord
        if (config.dataRecord) {
            config.etalonId = config.dataRecord.get('etalonId');
        }

        if (config.dataRecord && !config.metaRecord) {
            //TODO: load meta record
        }

        if (options.isReloadTimeline) {

            if (!config.timeIntervalStore) {
                throw new Error('time interval store is required');
            }

            promiseTimeline = Unidata.util.api.TimeInterval.getTimeline({
                drafts: config.drafts,
                operationId: config.operationId,
                store: config.timeIntervalStore,
                etalonId: config.etalonId
            });
        } else {
            deferred = new Ext.Deferred();
            deferred.resolve(config.timeIntervalStore);
            promiseTimeline = deferred.promise;
        }

        if (config.timeIntervalStore) {
            // calculate an appropriate time interval
            promiseFindTimeInterval = promiseTimeline.then(onGetTimelineFulfilled, onGetTimelineRejected);
            promiseFindTimeInterval.done();

            // load data record if it's not loaded
            promiseGetDataRecord = promiseFindTimeInterval.then(onFindTimeIntervalFulfilled, onFindTimeIntervalRejected);
            promiseGetDataRecord.done();
        } else {
            // получаем dataRecord без загрузки timeIntervals
            promiseGetDataRecord = Unidata.util.api.DataRecord.getDataRecord({
                drafts: config.drafts,
                operationId: config.operationId,
                etalonId: config.etalonId,
                date: config.timeIntervalDate
            });
            promiseGetDataRecord.done();
        }

        promiseOriginRecords = null;

        if (options.isLoadOriginRecords) {
            promiseOriginRecords = this.loadOrigin(config.etalonId, promiseTimeline, config.timeIntervalStore, config.originTimeIntervalDate);
        }
        promises.push(promiseOriginRecords);

        // load meta record if it's not loaded
        promiseGetMetaRecord = promiseGetDataRecord.then(onGetDataRecordFulfilled, onGetDataRecordRejected);
        promiseGetMetaRecord.done();

        promiseGetClassifierNode = promiseGetDataRecord.then(function (dataRecord) {
            return me.loadClassifierNodes(dataRecord);
        });

        promiseGetClassifierNode.then(onGetClassifierNodesFulfilled, onGetClassifierNodesRejected).done();
        promises.push(promiseGetClassifierNode);

        promiseRelationsDigest = null;

        if (options.isLoadBackRels) {
            promiseRelationsDigest = this.loadBackRels(promiseGetMetaRecord, config.etalonId);
        }
        promises.push(promiseRelationsDigest);

        promiseClusterCount = null;

        if (options.isLoadClusterCount) {
            promiseClusterCount = this.loadClusterCount(promiseGetMetaRecord, config.etalonId);
        }
        promises.push(promiseClusterCount);

        promiseReferenceRelations = null;

        if (options.isLoadReferenceRelations) {
            // relations
            promiseReferenceRelations = promiseGetMetaRecord.then(onGetMetaRecordFulfilled, onGetMetaRecordRejected);
            promiseReferenceRelations.done();
        }
        promises.push(promiseReferenceRelations);

        promiseAll = Ext.Deferred.all(promises);

        promiseResult = promiseAll.
        then(function (results) {
            if (results[0]) {
                newConfig.originRecords = results[0];
            }

            if (results[2]) {
                newConfig.relationsDigest = results[1];
            }

            if (results[3]) {
                newConfig.clusterCount = results[3];
            }

            return newConfig;
        });

        promiseResult.done();

        return promiseResult;
    },

    /**
     * Загрузка дата рекорда
     *
     * @param cfg
     * @param timeInterval
     * @param dataRecord
     * @param etalonId
     * @returns {*}
     */
    loadDataRecord: function (cfg, timeInterval, dataRecord, etalonId) {
        var result,
            isDatesInsideTimeInterval = false,
            validFrom,
            validTo,
            TimeIntervalUtil = Unidata.util.TimeInterval;

        cfg.timeInterval = timeInterval;

        // calculate if validFrom, validTo dates inside time interval
        if (dataRecord) {
            validFrom = dataRecord.get('validFrom');
            validTo   = dataRecord.get('validTo');

            isDatesInsideTimeInterval = TimeIntervalUtil.isDatesInsideTimeInterval(validFrom, validTo, timeInterval);
        }

        cfg.timeIntervalDate = Unidata.view.component.timeinterval.TimeIntervalDataView.getTimeIntervalDate(timeInterval);

        if (dataRecord && isDatesInsideTimeInterval) {
            result = dataRecord;
        } else {
            result = Unidata.util.api.DataRecord.getDataRecord({
                drafts: cfg.drafts,
                diffToDraft: cfg.diffToDraft,
                operationId: cfg.operationId,
                etalonId: etalonId,
                date: cfg.timeIntervalDate,
                intervalActive: timeInterval.get('active')
            });
        }

        return result;
    },

    /**
     * Загрузка метарекорда
     *
     * @param cfg
     * @param dataRecord
     * @param metaRecord
     * @returns {*}
     */
    loadMetaRecord: function (cfg, dataRecord, metaRecord) {
        var result,
            entityName = dataRecord.get('entityName'),
            entityType = dataRecord.get('entityType');

        cfg.dataRecord = dataRecord;

        if (!metaRecord) {
            result = Unidata.util.api.MetaRecord.getMetaRecord({
                entityName: entityName,
                entityType: entityType
            });
        } else {
            result = metaRecord;
        }

        return result;
    },

    /**
     * Загрузка связей типа ссылка
     *
     * @param cfg
     * @param metaRecord
     * @param etalonId
     * @returns {*}
     */
    loadReferenceRelations: function (cfg, metaRecord, etalonId) {
        var result;

        cfg.metaRecord = metaRecord;

        if (!Unidata.util.MetaRecord.isEntity(metaRecord)) {
            cfg.referenceRelations = [];

            result = cfg.referenceRelations;
        } else {
            result = Unidata.util.api.RelationReference.getAllRelationsTo({
                    drafts: cfg.drafts,
                    operationId: cfg.operationId,
                    metaRecord: metaRecord,
                    etalonId: etalonId,
                    interval: {
                        from: cfg.timeInterval.get('dateFrom'),
                        to: cfg.timeInterval.get('dateTo')
                    }
                })

                .then(function (referenceRelations) {
                    cfg.referenceRelations = referenceRelations;
                });
        }

        return result;
    },

    /**
     * Загрузка обратных ссылок
     *
     * @param cfg
     * @returns {*}
     */
    loadBackRelations: function (cfg) {
        var metaRecord = cfg.metaRecord,
            result;

        cfg.metaName = metaRecord.get('name');

        if (!Unidata.util.MetaRecord.isEntity(metaRecord)) {
            result = [];
        } else {
            result = Unidata.util.api.RelationsDigest.getAllRelationsDigest(cfg);
        }

        return result;
    },

    loadOriginRecords: function (cfg) {
        var result;

        result = Unidata.util.api.OriginRecord.getOriginRecords(cfg);

        return result;
    },

    loadOriginWithClassifierNodes: function (cfg) {
        var me = this,
            deferred = new Ext.Deferred(),
            promiseOrigin;

        promiseOrigin = this.loadOriginRecords(cfg);

        promiseOrigin.then(
            function (originRecords) {
                var promises = [];

                Ext.Array.each(originRecords, function (originRecord) {
                    var promise;

                    promise = me.loadClassifierNodes(originRecord);

                    promises.push(promise);
                });

                Ext.Deferred.all(promises).then(
                    function (originRecordsClassifierNodes) {
                        var result;

                        result = {
                            origins: originRecords,
                            originsClassifierNodes: originRecordsClassifierNodes
                        };

                        deferred.resolve(result);
                    },
                    function () {
                        deferred.reject();
                    }
                );
            },
            function () {
                deferred.reject();
            }
        );

        return deferred.promise;
    },

    loadClassifierNodes: function (dataRecord) {
        var classifiers    = dataRecord.classifiers(),
            cfgClassifiers = [],
            promise;

        classifiers.each(function (classifier) {
            if (classifier.get('classifierNodeId')) {
                cfgClassifiers.push({
                    classifierName: classifier.get('classifierName'),
                    classifierNodeId: classifier.get('classifierNodeId')
                });
            }
        });

        promise = Unidata.util.api.Classifier.getClassifierNodes(cfgClassifiers, 'DATA');

        return promise;
    }
});

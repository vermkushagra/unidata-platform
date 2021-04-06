/**
 * API загрузки relations digest
 */

Ext.define('Unidata.util.api.RelationsDigest', {
    singleton: true,

    /**
     * Получить relationsDigest по всем связанным метамоделям
     *
     * cfg:
     * metaName - имя сущности
     * etalonId - идентификатор эталона
     * dateFrom - начальная граница периода актуальности
     * dateTo - конечная граница периода актуальности
     * relDirection - направление relations (from, to)
     *
     * @param cfg
     * @returns {*}
     */
    getAllRelationsDigest: function (cfg) {
        var promise,
            promise1,
            me = this;

        promise = Unidata.util.api.RelationViewMetaRecord.loadRelationViewMetaRecords(cfg);

        promise1 = promise.then(function (relationViewMetaRecords) {
                return me.loadAllRelationsDigestByRelationViewMetaRecords({
                    etalonId: cfg.etalonId,
                    dateFrom: cfg.dateFrom,
                    dateTo: cfg.dateTo,
                    relDirection: cfg.relDirection,
                    relationViewMetaRecords: relationViewMetaRecords
                });
            });

        promise.done();

        return promise1;
    },

    /**
     * Загрузить все relation digest на основании связанных метасущностей (metaRelations)
     *
     * cfg:
     * etalonId - идентификатор эталона
     * dateFrom - начальная граница периода актуальности
     * dateTo - конечная граница периода актуальности
     * relDirection - направление relations (from, to)
     * relationViewMetaRecords - связанные метасущности с представлением связей
     *
     * @param cfg
     * @returns {*|boolean|Ext.promise.Promise|Ext.Promise}
     */
    loadAllRelationsDigestByRelationViewMetaRecords: function (cfg) {
        var relationViewMetaRecords = cfg.relationViewMetaRecords,
            me = this,
            promise,
            promises = [],
            promiseAll;

        relationViewMetaRecords.forEach(function (relationViewMetaRecord) {
            relationViewMetaRecord.relations().each(function (relation) {
                    promise = me.loadRelationsDigestByRelationViewMetaRecord({
                        etalonId: cfg.etalonId,
                        relation: relation,
                        dateFrom: cfg.dateFrom,
                        dateTo: cfg.dateTo,
                        relDirection: cfg.relDirection,
                        relationViewMetaRecord: relationViewMetaRecord
                    });
                    promises.push(promise);
                });
        });

        // ждём, когда все relationsDigest загрузятся
        promiseAll = Ext.Deferred.all(promises);

        return promiseAll;
    },

    /**
     * Загрузить relations digest на основании мета-информации о связи
     *
     * cfg:
     * etalonId - идентификатор эталона
     * relation - имя связи
     * dateFrom - начальная граница периода актуальности
     * dateTo - конечная граница периода актуальности
     * relDirection - конечная граница периода актуальности
     * relationViewMetaRecord - мета-информация о связи
     *
     * @param cfg
     * @returns {null|*|promise.promise|jQuery.promise|Ext.promise|d.promise}
     */
    loadRelationsDigestByRelationViewMetaRecord: function (cfg) {
        var store,
            filters = [{property: 'displayable', value: true}],
            fields = Unidata.util.UPathMeta.buildAttributePaths(cfg.relationViewMetaRecord, filters),
            deferred = new Ext.Deferred(),
            relation = cfg.relation,
            relName = relation.get('name');

        store = this.createRelationsDigestStore({
            etalonId: cfg.etalonId,
            relName: relName,
            // DON'T DELETE! Параметры для полноценного запроса периода
            //from: parentSelectionPeriod.get('dateFrom'),
            //to: parentSelectionPeriod.get('dateTo'),
            from: cfg.dateFrom,
            to: cfg.dateTo,
            fields: fields,
            direction: cfg.relDirection.toUpperCase(),
            totalCount: true
        });

        store.on('load', function (store, records, successful) {
            var reader,
                hasRecords;

            if (!successful) {
                deferred.reject();
            } else {
                reader = store.getProxy().getReader();
                hasRecords = reader.rawData ? reader.rawData.hasRecords : false;

                deferred.resolve({
                    relation: relation,
                    relationViewMetaRecord: cfg.relationViewMetaRecord,
                    store: store,
                    hasRecords: hasRecords
                });
            }
        });

        store.load();

        return deferred.promise;
    },

    createRelationsDigestStore: function (params) {
        var store,
            proxyConfig;

        proxyConfig = {
            type: 'data.relationproxy',
            url: Unidata.Config.getMainUrl() + 'internal/data/relations/digest',
            limitParam: 'count',
            startParam: '',
            paramsAsJson: true,
            extraParams: params,
            actionMethods: {
                create: 'POST',
                read: 'POST',
                update: 'POST',
                destroy: 'POST'
            },

            reader: {
                rootProperty: 'hits',
                totalProperty: 'total_count'
            }
        };

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.search.SearchHit',
            pageSize: 10,
            proxy: proxyConfig
        });

        return store;
    }
});

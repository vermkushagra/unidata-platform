/**
 * API для связей типа ссылка
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-01
 */

Ext.define('Unidata.util.api.RelationReference', {

    singleton: true,

    getAllRelationsTo: function (cfg) {
        var metaRecord        = cfg.metaRecord,
            etalonId          = cfg.etalonId,
            interval          = cfg.interval,
            drafts            = cfg.drafts,
            operationId       = cfg.operationId,
            referenceRelation = metaRecord.relations().query('relType', 'References', false, false, true),
            me                = this,
            promises          = [];

        // грузим все связи
        referenceRelation.each(function (metaRelation) {

            var promise;

            promise = me.getOneRelationTo({
                drafts:       drafts,
                operationId:  operationId,
                etalonId:     etalonId,
                relationName: metaRelation.get('name'),
                interval:     interval
            })

            // success request
            .then(function (dataRelation) {
                return {
                    meta: metaRelation,
                    data: dataRelation
                };
            });

            promise.done();

            promises.push(promise);

        });

        // когда все связи прогрузятся, ресолвим или реджектим
        return Ext.Deferred.all(promises);

    },

    getOneRelationTo: function (cfg) {
        var me       = this,
            interval = cfg.interval,
            drafts   = cfg.drafts,
            operationId = cfg.operationId,
            promise;

        // грузим таймлайн
        promise = me.getRelationTimeline({
            drafts:       drafts,
            operationId:  operationId,
            etalonId:     cfg.etalonId,
            relationName: cfg.relationName,
            interval:     interval
        })

        // успешно загрузили таймлайн для связи
        .then(function (records) {
            return me.loadRelationsByTimelineRecords({
                drafts:   drafts,
                operationId: operationId,
                records:  records,
                interval: interval
            });
        });

        return promise;
    },

    getRelationTimeline: function (cfg) {
        var url,
            store,
            dateFrom,
            dateTo,
            deferred     = new Ext.Deferred(),
            dateFormat   = Unidata.Config.getDateTimeFormatProxy(),

            drafts       = cfg.drafts,
            operationId  = cfg.operationId,
            etalonId     = cfg.etalonId,
            relationName = cfg.relationName,
            interval     = cfg.interval,
            extraParams = {
                drafts: drafts
            };

        if (operationId) {
            extraParams.operationId = operationId;
        }

        dateFrom = Ext.Date.format(interval.from, dateFormat) || 'null';
        dateTo   = Ext.Date.format(interval.to,   dateFormat) || 'null';

        url = Unidata.Config.getMainUrl() + 'internal/data/relations/timeline/' +
            etalonId + '/' + relationName +
            '/' + dateFrom + '/' + dateTo;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.data.RelationTimeline',
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
                deferred.resolve(records);
            }

        });

        store.load();

        return deferred.promise;

    },

    loadRelationsByTimelineRecords: function (cfg) {
        var records = cfg.records,
            interval = cfg.interval,
            drafts = cfg.drafts,
            operationId = cfg.operationId,
            promise = null,
            me = this,
            loadRelationToCfg;

        if (records.length === 0) {
            return promise;
        }

        loadRelationToCfg = this.findRelationAndActiveTimeInterval(records, interval);
        loadRelationToCfg.drafts = drafts;
        loadRelationToCfg.operationId = operationId;

        if (!loadRelationToCfg.record) {
            throw new Error(Unidata.i18n.t('util>noRelationReferenceData'));
        }

        promise = me.loadRelationTo(loadRelationToCfg);

        return promise;
    },

    /**
     * Поиск первого relation у которого имеется активный временной интервал для референсной даты родительского
     * временного интервала (from или to), а также активного временного интервала
     *
     * Должен найтись хотя бы один такой интервал
     *
     * @param relations
     * @param interval
     */
    findRelationAndActiveTimeInterval: function (relations, interval) {
        var date = interval.from || interval.to,
            foundRelation = null,
            foundTimeInterval = null,
            cfg;

        function findFirstActiveTimeInterval (timeintervals) {
            var foundIndex,
                foundTimeInterval = null;

            foundIndex = timeintervals.findBy(function (timeinterval) {
                return timeinterval.get('active') == true;
            });

            if (foundIndex > -1) {
                foundTimeInterval = timeintervals.getAt(foundIndex);
            }

            return foundTimeInterval;
        }

        foundRelation = Ext.Array.findBy(relations, function (relation) {
            var timeintervals;

            if (!relation) {
                return false;
            }

            timeintervals = relation.timeline();

            foundTimeInterval = findFirstActiveTimeInterval(timeintervals, date);

            if (foundTimeInterval) {
                return true;
            }
        });

        cfg = {
            record: foundRelation,
            interval: foundTimeInterval
        };

        return cfg;
    },

    loadRelationTo: function (cfg) {
        var interval = cfg.interval,
            item     = cfg.record,
            drafts   = cfg.drafts,
            operationId = cfg.operationId,
            dateFrom = interval.get('dateFrom'),
            dateTo   = interval.get('dateTo'),
            relationReference,
            proxy,
            deferred = new Ext.Deferred();

        relationReference  = new Unidata.model.data.RelationReference();
        proxy  = relationReference.getProxy();

        proxy.dateFrom = dateFrom || dateTo;
        proxy.etalonId = item.get('etalonId');

        proxy.setExtraParam('drafts', drafts);

        if (operationId) {
            proxy.setExtraParam('operationId', operationId);
        } else {
            delete proxy.extraParams.operationId;
        }

        // загружаем связь, создаём promise
        relationReference.load({
            success: function () {
                relationReference.set({
                    validFrom: dateFrom,
                    validTo: dateTo
                });
                deferred.resolve(relationReference);
            },
            failure: deferred.reject.bind(deferred)
        });

        return deferred.promise;
    },

    remove: function (relationTo) {
        var etalonId = relationTo.get('etalonId'),
            dateFrom = relationTo.get('validFrom'),
            dateTo = relationTo.get('validTo'),
            deferred = new Ext.Deferred(),
            dateFormat = Unidata.Config.getDateTimeFormatProxy();

        dateFrom = dateFrom ? Ext.Date.format(dateFrom, dateFormat) : 'null';
        dateTo = dateTo ? Ext.Date.format(dateTo, dateFormat) : 'null';

        Ext.Ajax.request({
            method: 'DELETE',
            url: Unidata.Config.getMainUrl() +
                    'internal/data/relations/relation/version/' + etalonId + '/' + dateFrom + '/' + dateTo,
            success: function () {
                deferred.resolve(relationTo);
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;

    },

    /**
     *
     * @param {Unidata.model.data.RelationReference} relationTo - модель связи
     * @param {Number} etalonId - id записи, которой принадлежит связь
     *
     * @returns {Ext.promise.Promise}
     */
    save: function (relationTo, etalonId) {
        var data = relationTo.getFilteredData({associated: true, serialize: true}),
            deferred = new Ext.Deferred();

        Ext.Ajax.request({
            method: 'POST',
            url: Unidata.Config.getMainUrl() + 'internal/data/relations/relation/relto/' + etalonId,
            jsonData: Ext.util.JSON.encode(data),
            success: function (response, opts) {

                var reponseJson;

                if (opts.jsonData) {
                    reponseJson = JSON.parse(response.responseText);

                    if (reponseJson.success) {
                        relationTo.set('etalonId', reponseJson.content.etalonId);
                    }

                }

                deferred.resolve(relationTo);

            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;

    }

});

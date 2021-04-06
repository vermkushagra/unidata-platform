/**
 * API для связей включение
 *
 * @author Aleksandr Bavin
 * @date 2016-05-17
 */

Ext.define('Unidata.util.api.RelationContains', {

    singleton: true,

    /**
     * Получает мета записи зависимостей типа включение
     *
     * @param metaRecord
     * @returns {*}
     */
    getMetaRelations: function (metaRecord) {
        return metaRecord.relations().query('relType', 'Contains');
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
            extraParams  = {
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
                extraParams: extraParams,
                url: url,
                reader: {
                    rootProperty: 'content'
                }
            }
        });

        // подгружаем информацию о связи
        store.on('load', function (store, relation, successful) {

            if (!successful) {
                deferred.reject();
            } else {
                // удаляем записи из стора чтоб его смог собрать GC
                store.removeAll(true);

                deferred.resolve(relation);
            }

        });

        store.load();

        return deferred.promise;
    },

    /**
     * Цепляет фильтр к timelineStore
     *
     * @param timelineStore
     */
    applyFilterToTimeIntervalStore: function (timelineStore) {
        var filter;

        filter = new Ext.util.Filter({
            filterFn: function (item) {
                var active = item.get('active'),
                    contributors;

                contributors = item.contributors().getRange();

                if (!active) {
                    active = Ext.Array.some(contributors, function (contributor) {
                        return contributor.status === 'INACTIVE' && contributor.approval === 'PENDING';
                    });
                }

                return active;
            }
        });

        timelineStore.setRemoteFilter(false);
        timelineStore.clearFilter();
        timelineStore.addFilter(filter);
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
            relation   = Ext.create('Unidata.model.data.RelationContains'),
            proxy      = relation.getProxy();

        proxy.setUrl(Unidata.Config.getMainUrl() + 'internal/data/relations/relation');
        proxy.etalonId = etalonId;
        proxy.dateFrom = dateFrom || dateTo;

        proxy.setExtraParam('drafts', drafts);

        if (operationId) {
            proxy.setExtraParam('operationId', operationId);
        } else {
            delete proxy.extraParams.operationId;
        }

        // загружаем связь
        relation.load({
            success: function (relation) {
                deferred.resolve(
                    relation
                );
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    }

});

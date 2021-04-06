/**
 * API загрузки origin записей
 */

Ext.define('Unidata.util.api.OriginRecord', {
    singleton: true,

    /**
     * Загрузить origin records для указанного etalonId и даты
     *
     * cfg:
     * etalonId - ID эталона
     * date - дата
     *
     * @param cfg
     * @returns {null|*|promise.promise|jQuery.promise|Ext.promise|d.promise}
     */
    getOriginRecords: function (cfg) {
        var deferred,
            store;

        store = this.createStore(cfg);
        deferred = new Ext.Deferred();

        store.on('load', function (store, originRecords, successful) {
            if (!successful) {
                deferred.reject();
            } else {
                deferred.resolve(originRecords);
            }
        });

        store.load();

        return deferred.promise;
    },

    /**
     * Cоздать store загрузки originRecords
     *
     * @param cfg
     * @param cfg.date
     * @param cfg.etalonId
     * @param cfg.operationId
     * @param cfg.drafts
     * @param {boolean} [cfg.paged]
     * @returns {Ext.data.Store|*}
     */
    createStore: function (cfg) {
        var store,
            proxyCfg,
            date = cfg.date,
            etalonId = cfg.etalonId,
            operationId = cfg.operationId,
            extraParams = {
                drafts: Boolean(cfg.drafts)
            },
            baseUrl = Unidata.Config.getMainUrl(),
            url,
            rootProperty;

        if (cfg.paged) {
            rootProperty = 'content.content';
            url = baseUrl + 'internal/data/entities/paged/origin';
        } else {
            rootProperty = 'content';
            url = baseUrl + 'internal/data/entities/origin';
        }

        date = date || null;

        if (operationId) {
            extraParams.operationId = operationId;
        }

        proxyCfg = {
            type: 'data.recordproxy',
            url: url,
            dateFormat: Unidata.Config.getDateTimeFormatProxy(),
            extraParams: extraParams,
            limitParam: 'limit',
            startParam: 'start',
            pageParam: 'page',
            reader: {
                totalProperty: 'content.totalElements',
                rootProperty: rootProperty
            }
        };

        store = Ext.create('Ext.data.Store',
        {
            model: 'Unidata.model.data.OriginRecord',
            pageSize: Unidata.Config.getOriginCarouselDisplayCount(),
            proxy: proxyCfg
        });

        store.getProxy().setDate(date);
        store.getProxy().setEtalonId(etalonId);

        return store;
    },

    detachOrigin: function (originId) {
        var mainUrl = Unidata.Config.getMainUrl(),
            urlTpl = '{0}internal/data/entities/detach-origin/{1}',
            deferred,
            url;

        deferred = new Ext.Deferred();

        url = Ext.String.format(urlTpl, mainUrl, originId);

        Ext.Ajax.unidataRequest({
            url: url,
            method: 'GET',
            headers: {'Content-Type': 'application/json'},
            params: {},
            success: function (response) {
                var responseJson;

                responseJson = Ext.util.JSON.decode(response.responseText, true);

                if (responseJson.success === true && responseJson.content) {
                    deferred.resolve(responseJson.content.etalonId);
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

/**
 * API взаимодействия с TimeInterval
 */

Ext.define('Unidata.util.api.TimeInterval', {
    singleton: true,

    createStore: function (customConfig) {
        var store,
            url = Unidata.Config.getMainUrl() + 'internal/data/entities/timeline',
            config;

        config = {
            model: 'Unidata.model.data.TimeInterval',
            sorters: [{
                property: 'dateFrom',
                direction: 'ASC'
            }
            ],
            filters: [],
            proxy: {
                type: 'rest',
                url: url,
                reader: {
                    rootProperty: 'content.timeline'
                }
            }
        };

        config = Ext.apply(config, customConfig);

        store = Ext.create('Ext.data.Store', config);

        store.clearFilter();

        store.getFilters().add(this.getFilter());

        return store;
    },

    getFilter: function () {
        if (!this.filter) {
            this.filter = new Ext.util.Filter({
                filterFn: function (item) {
                    var active = item.get('active'),
                        selected = item.get('chosen'),
                        contributors;

                    contributors = item.contributors().getRange();

                    if (!active) {
                        active = Ext.Array.some(contributors, function (contributor) {
                            return contributor.status === 'INACTIVE' && contributor.approval === 'PENDING';
                        });
                    }

                    return active || selected;
                }
            });
        }

        return this.filter;
    },

    getTimeline: function (cfg) {
        var deferred,
            drafts = cfg.drafts,
            operationId = cfg.operationId,
            store = cfg.store,
            etalonId = cfg.etalonId,
            proxy,
            oldUrl;

        if (!store) {
            store = this.createStore();
        }

        proxy = store.getProxy();
        proxy.setExtraParam('drafts', Boolean(drafts));

        if (operationId) {
            proxy.setExtraParam('operationId', operationId);
        } else {
            delete proxy.extraParams.operationId;
        }

        oldUrl = proxy.getUrl();
        proxy.setUrl(proxy.getUrl() + '/' + etalonId);

        deferred = new Ext.Deferred();

        store.on('load', function (store, records, successful) {
            if (successful) {
                deferred.resolve(store);
            } else {
                deferred.reject();
            }
        });

        store.clearStoreData();
        store.reload();
        proxy.setUrl(oldUrl);

        return deferred.promise;
    },

    deleteTimeInterval: function (etalonId, dateFrom, dateTo) {
        var url,
            tplUrl = '{0}internal/data/entities/version/{1}/{2}/{3}',
            promise;

        url = this.buildTimeIntervalUrl(etalonId, dateFrom, dateTo, tplUrl);
        promise = this.deleteAbstractTimeInterval(url, etalonId);

        return promise;
    },

    restoreTimeInterval: function (etalonId, dateFrom, dateTo) {
        var url,
            tplUrl = '{0}internal/data/entities/period-restore/{1}/{2}/{3}',
            promise;

        url = this.buildTimeIntervalUrl(etalonId, dateFrom, dateTo, tplUrl);
        promise = this.restoreAbstractTimeInterval(url, etalonId);

        return promise;
    },

    deleteRelationTimeInterval: function (relationEtalonId, dateFrom, dateTo) {
        var url,
            tplUrl = '{0}internal/data/relations/relation/version/{1}/{2}/{3}',
            promise;

        url = this.buildTimeIntervalUrl(relationEtalonId, dateFrom, dateTo, tplUrl);
        promise = this.deleteAbstractTimeInterval(url, relationEtalonId);

        return promise;
    },

    buildTimeIntervalUrl: function (etalonId, dateFrom, dateTo, tplUrl) {
        var url,
            mainUrl,
            dateTimeFormatProxy = Unidata.Config.getDateTimeFormatProxy();

        // build url
        dateFrom = dateFrom ? Ext.Date.format(dateFrom, dateTimeFormatProxy) : 'null';
        dateTo = dateTo ? Ext.Date.format(dateTo, dateTimeFormatProxy) : 'null';

        mainUrl = Unidata.Config.getMainUrl();
        url = Ext.String.format(tplUrl, mainUrl, etalonId, dateFrom, dateTo, tplUrl); //TODO: encodeURLcomponents

        return url;
    },

    deleteAbstractTimeInterval: function (url, etalonId) {
        var deferred = new Ext.Deferred();

        Ext.Ajax.unidataRequest({
            method: 'DELETE',
            url: url,
            success: function () {
                deferred.resolve(etalonId);
            },
            failure: function () {
                deferred.reject();
            },
            scope: this
        });

        return deferred.promise;
    },
    restoreAbstractTimeInterval: function (url, etalonId) {
        var deferred = new Ext.Deferred();

        Ext.Ajax.unidataRequest({
            method: 'PUT',
            url: url,
            success: function () {
                deferred.resolve(etalonId);
            },
            failure: function () {
                deferred.reject();
            },
            scope: this
        });

        return deferred.promise;
    }
});

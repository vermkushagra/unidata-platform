/**
 * API взаимодействия с MetaRecord
 */

Ext.define('Unidata.util.api.MetaRecord', {
    singleton: true,

    /**
     * Загрузить метамодель
     *
     * cfg:
     * entityName - имя метамодели
     * entityType - тип метамодели
     *
     * @param cfg
     * @returns {null|*|promise.promise|jQuery.promise|Ext.promise|d.promise}
     */
    getMetaRecord: function (cfg) {
        var deferred,
            entityName = cfg.entityName,
            entityType = cfg.entityType,
            url,
            metaRecord,
            proxy,
            oldUrl,
            className,
            lookupPostfix,
            mainUrl    = Unidata.Config.getMainUrl();

        lookupPostfix = (entityType === 'LookupEntity') ? 'lookup-' : '';
        className     = Ext.String.format('Unidata.model.entity.{0}', entityType);
        url           = Ext.String.format('{0}internal/meta/{1}entities', mainUrl, lookupPostfix);

        deferred = new Ext.Deferred();

        metaRecord = Ext.create(className);
        proxy      = metaRecord.getProxy();
        oldUrl     = proxy.getUrl();

        proxy.setUrl(url);
        metaRecord.setId(entityName);

        metaRecord.load({
            params: {
                draft: Boolean(cfg.draft)
            },
            success: function (metaRecord) {
                deferred.resolve(metaRecord);
            },
            failure: function () {
                deferred.reject();
            }
        });

        // restore url
        proxy.setUrl(oldUrl);

        return deferred.promise;
    },

    getMetaRecords: function (cfg) {
        var deferred,
            store;

        store = this.createMetaRecordsStore(cfg);
        deferred = new Ext.Deferred();

        store.on('load', function (store, metaRecords, successful) {
            if (!successful) {
                deferred.reject();
            } else {
                deferred.resolve(metaRecords);
            }
        });

        store.load();

        return deferred.promise;
    },

    createMetaRecordsStore: function (cfg) {
        var mainUrl = Unidata.Config.getMainUrl(),
            urlTpl = '{0}internal/meta/{1}entities',
            entityType = cfg.entityType,
            store,
            className,
            lookupPostfix,
            url;

        lookupPostfix = (entityType === 'LookupEntity') ? 'lookup-' : '';
        className     = Ext.String.format('Unidata.model.entity.{0}', entityType);
        url           = Ext.String.format(urlTpl, mainUrl, lookupPostfix);

        store = Ext.create('Ext.data.Store', {
            model: className,
            proxy: {
                type: 'rest',
                url: url,
                extraParams: {
                    size: 10000
                },
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        });

        return store;
    }
});

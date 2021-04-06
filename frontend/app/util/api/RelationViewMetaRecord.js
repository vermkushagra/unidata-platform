Ext.define('Unidata.util.api.RelationViewMetaRecord', {
    singleton: true,

    createStore: function (cfg) {
        var store,
            urlPrefix      = 'internal/meta/relations',
            metaName = cfg.metaName,
            relDirection = cfg.relDirection,
            url,
            mainUrl = Unidata.Config.getMainUrl();

        url = Ext.String.format('{0}{1}/{2}/{3}', mainUrl, urlPrefix, relDirection, metaName);

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.entity.Entity',
            proxy: {
                type: 'ajax',
                url: url,
                reader: {
                    rootProperty: 'content'
                }
            }
        });

        return store;
    },

    loadRelationViewMetaRecords: function (cfg) {
        var store,
            deferred;

        store = this.createStore(cfg);
        deferred = new Ext.Deferred();

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
    }
});

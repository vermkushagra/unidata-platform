/**
 * API загрузки cleanse functions
 *
 * @author Sergey Shishigin
 */

Ext.define('Unidata.util.api.SourceSystem', {
    singleton: true,

    /**
     * Загрузка списка source systems
     * @returns {Ext.promise}
     */
    loadSourceSystems: function () {
        var deferred,
            store;

        deferred = new Ext.Deferred();

        store = this.createSourceSystemStore();
        store.on('load', function (store, records, successful) {
            if (successful) {
                deferred.resolve(records);
            } else {
                deferred.reject();
            }
        });
        store.load();

        return deferred.promise;
    },

    /**
     * Создать store
     * @returns {Ext.data.Store}
     */
    createSourceSystemStore: function () {
        var store;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.sourcesystem.SourceSystem',
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: Unidata.Config.getMainUrl() + 'internal/meta/source-systems',
                reader: {
                    type: 'json',
                    rootProperty: 'sourceSystem'
                }
            }
        });

        return store;
    }
});

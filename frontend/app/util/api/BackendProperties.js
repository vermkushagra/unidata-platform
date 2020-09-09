/**
 * API взаимодействия с backend properties
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.util.api.BackendProperties', {
    singleton: true,

    getBackendProperties: function () {
        var url = Unidata.Config.getMainUrl() + 'internal/configuration',
            deferred,
            store;

        deferred = new Ext.Deferred();

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.beproperties.BackendProperties',
            proxy: {
                type: 'rest',
                url: url,
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        });

        store.on('load', function (store, backendProperties, successful) {
            if (!successful) {
                deferred.reject();
            } else {
                deferred.resolve(backendProperties);
            }
        });

        store.load();

        return deferred.promise;
    },

    saveBackendProperties: function (backendProperties) {
        var data = {},
            url = Unidata.Config.getMainUrl() + 'internal/configuration',
            deferred;

        Ext.Array.each(backendProperties, function (backendProperty) {
            data[backendProperty.get('name')] = backendProperty.get('value');
        });

        deferred = new Ext.Deferred();

        Ext.Ajax.request({
            url: url,
            method: 'PUT',
            success: function () {
                deferred.resolve();
            },
            failure: function () {
                deferred.reject();
            },
            jsonData: Ext.util.JSON.encode(data),
            scope: this
        });

        return deferred.promise;
    }
});

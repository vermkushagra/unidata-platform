/**
 * API взаимодействия с MetaModel (полная совокупность metaRecord)
 *
 * @author Ivan Marshalkin
 * @date 2017-09-20
 */

Ext.define('Unidata.util.api.MetaModel', {
    singleton: true,

    /**
     * Возвращает данные по метамодели (совокупности всех метарекордов)
     *
     * @returns {null|*|promise.promise|jQuery.promise|Ext.promise|d.promise}
     */
    getMetaModelData: function (cfg) {
        var mainUrl = Unidata.Config.getMainUrl(),
            deferred,
            url;

        url = Ext.String.format('{0}internal/meta/model/model_name', mainUrl);

        deferred = new Ext.Deferred();

        Ext.Ajax.request({
            method: 'GET',
            url: url,
            params: {
                draft: Boolean(cfg.draft)
            },
            success: function (data) {
                var response = Ext.decode(data.responseText, true);

                if (response && response.success) {
                    deferred.resolve(Ext.clone(response.content));
                } else {
                    deferred.reject();
                }
            },
            failure: function () {
                deferred.reject();
            },
            scope: this
        });

        return deferred.promise;
    },

    /**
     * Сохраняет имя метамодели (совокупности всех метарекордов)
     *
     * @returns {null|*|promise.promise|jQuery.promise|Ext.promise|d.promise}
     */
    saveMetaModelName: function (name, cfg) {
        var mainUrl = Unidata.Config.getMainUrl(),
            deferred,
            url,
            data;

        url = Ext.String.format('{0}internal/meta/model/model_name', mainUrl);

        deferred = new Ext.Deferred();

        data = {
            name: name,
            storageId: null
        };

        Ext.Ajax.request({
            url: url,
            method: 'POST',
            params: {
                draft: Boolean(cfg.draft)
            },
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

/**
 * API взаимодействия с черновиком метамодели
 *
 * @author Ivan Marshalkin
 * @date 2017-09-20
 */

Ext.define('Unidata.util.api.Draft', {
    singleton: true,

    /**
     * Публикует черновик
     *
     * @returns {null|*|promise.promise|jQuery.promise|Ext.promise|d.promise}
     */
    applyDraft: function () {
        var mainUrl = Unidata.Config.getMainUrl(),
            data = {},
            deferred,
            url;

        url = Ext.String.format('{0}internal/meta/model/apply_draft', mainUrl);

        deferred = new Ext.Deferred();

        Ext.Ajax.unidataRequest({
            url: url,
            method: 'POST',
            timeout: 1000 * 60 * 60 * 5, //таймаут на применение черновика 5 часов
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
    },

    /**
     * Удаляет черновик
     *
     * @returns {null|*|promise.promise|jQuery.promise|Ext.promise|d.promise}
     */
    removeDraft: function () {
        var mainUrl = Unidata.Config.getMainUrl(),
            data = {},
            deferred,
            url;

        url = Ext.String.format('{0}internal/meta/model/remove_draft', mainUrl);

        deferred = new Ext.Deferred();

        Ext.Ajax.request({
            url: url,
            method: 'POST',
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

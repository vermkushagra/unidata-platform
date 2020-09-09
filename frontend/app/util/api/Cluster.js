/**
 * API взаимодействия с кластерами
 *
 * @author Sergey Shishigin
 * @date 2016-11-01
 */

Ext.define('Unidata.util.api.Cluster', {
    extend: 'Unidata.util.api.AbstractApi',
    singleton: true,

    blockClusterRecord: function (cfg) {
        var url,
            deferred,
            entityName = cfg.entityName,
            clusterId = cfg.clusterId,
            etalonId = cfg.etalonId,
            params;

        deferred = Ext.create('Ext.Deferred');

        if (!etalonId || !entityName || !clusterId) {
            deferred.reject();

            return deferred.promise;
        }

        url = Unidata.Api.getBlockClusterRecordUrl(entityName, clusterId);

        params = {
            etalonId: etalonId
        };

        Ext.Ajax.request({
            url: url,
            method: 'PUT',
            success: function (response) {
                var responseJson = this.parseClusterResponse(response);

                deferred.resolve(responseJson);
            },
            failure: function () {
                deferred.reject();
            },
            params: params,
            scope: this
        });

        return deferred.promise;
    },

    parseClusterResponse: function (response) {
        var responseJson;

        responseJson = Ext.util.JSON.decode(response.responseText, true);

        return responseJson;
    },

    /**
     * Возвращает количество кластеров
     *
     * @param entityName
     * @param groupId
     * @param ruleId
     * @param etalonId
     * @param {boolean} [preprocessing]
     */
    getClusterCount: function (entityName, groupId, ruleId, etalonId, preprocessing) {
        var deferred = Ext.create('Ext.Deferred'),
            url      = Unidata.Api.getClusterCountUrl(entityName),
            params;

        params = {
            //entityName: entityName
        };

        if (groupId) {
            params['groupId'] = groupId;
        }

        if (ruleId) {
            params['ruleId'] = ruleId;
        }

        if (etalonId) {
            params['etalonId'] = etalonId;
        }

        params['preprocessing'] = Boolean(preprocessing);

        Ext.Ajax.request({
            method: 'GET',
            url: url,
            success: function (response) {
                var count = null,
                    responseJson;

                responseJson = Ext.util.JSON.decode(response.responseText, true);

                if (responseJson) {
                    count = responseJson.content;
                }

                deferred.resolve(count);
            },
            failure: function () {
                deferred.reject();
            },
            scope: this,
            params: params
        });

        return deferred.promise;
    }
});

/**
 * API взаимодействия с merge
 *
 * @author Sergey Shishigin
 * @date 2016-10-22
 */

Ext.define('Unidata.util.api.Merge', {
    extend: 'Unidata.util.api.AbstractApi',
    singleton: true,

    getMergePreview: function (cfg) {
        var url = Unidata.Api.getMergeUrl(),
            deferred = Ext.create('Ext.Deferred'),
            params,
            etalonIds = cfg.etalonIds;

        params = {
            id: etalonIds
        };

        Ext.Ajax.request({
            method: 'GET',
            url: url,
            success: function (response) {
                var result;

                result = this.parseMergePreviewResponse(response);

                deferred.resolve(result);
            },
            failure: function () {
                deferred.reject();
            },
            scope: this,
            params: params
        });

        return deferred.promise;
    },

    /**
     * Консолидировать
     *
     * cfg:
     * dataRecordKeys (Unidata.model.data.DataRecordKey[]/String[])
     *
     * @param cfg
     * @returns {*}
     */
    doMerge: function (cfg) {
        var url = Unidata.Api.getMergeUrl(),
            deferred = Ext.create('Ext.Deferred'),
            dataRecordKeys = cfg.dataRecordKeys,
            etalonIds,
            winnerEtalonId = cfg.winnerEtalonId,
            params;

        etalonIds = this.retrieveEtalonIds(dataRecordKeys);

        if (!etalonIds) {
            return null;
        }

        params = {
            id: etalonIds,
            winnerEtalonId: winnerEtalonId
        };

        Ext.Ajax.request({
            url: url,
            method: 'PUT',
            success: function (response) {
                var result;

                result = this.parseDoMergeResponse(response);
                deferred.resolve(result);
            },
            failure: function () {
                deferred.reject();
            },
            params: params,
            scope: this
        });

        return deferred.promise;
    },

    parseDoMergeResponse: function (response) {
        var result,
            responseJson,
            winnerEtalonId;

        responseJson = Ext.util.JSON.decode(response.responseText, true);
        winnerEtalonId = responseJson.content;

        result = {
            winnerEtalonId: winnerEtalonId
        };

        return result;
    },

    /**
     *
     * @param dataRecordKeys
     * @returns {*}
     */
    retrieveEtalonIds: function (dataRecordKeys) {
        var etalonIds,
            DataRecordUtil = Unidata.util.DataRecord;

        if (!dataRecordKeys || !Ext.isArray(dataRecordKeys) || dataRecordKeys.length === 0) {
            return null;
        }

        if (dataRecordKeys[0] instanceof Unidata.model.data.DataRecordKey) {
            etalonIds = DataRecordUtil.pluckEtalonIds(dataRecordKeys);
        } else if (Ext.isString(dataRecordKeys[0])) {
            etalonIds = dataRecordKeys;
        } else {
            return null;
        }

        return etalonIds;
    },

    parseMergePreviewResponse: function (response) {
        var mergePreview = {},
            responseJson,
            previewRecord,
            data,
            reader,
            resultSet;

        responseJson = Ext.util.JSON.decode(response.responseText);

        if (responseJson.record) {
            data = responseJson.record;

            reader = Ext.create('Ext.data.JsonReader', {
                model: 'Unidata.model.data.Record'
            });

            resultSet = reader.readRecords(data);

            if (resultSet && resultSet.count > 0) {
                previewRecord = resultSet.records[0];
            }

            previewRecord.setId(null);

            mergePreview.previewRecord = previewRecord;
        }

        if (responseJson.attributeWinnersMap) {
            mergePreview.attributeWinnersMap = responseJson.attributeWinnersMap;
        }

        if (responseJson.winnerEtalonId) {
            mergePreview.winnerEtalonId = responseJson.winnerEtalonId;
        }

        return mergePreview;
    }
});

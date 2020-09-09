/**
 * API тестирования правил качества
 *
 * @author Ivan Marshalkin
 * @date 2018-03-02
 */

Ext.define('Unidata.util.api.DataQualitySandbox', {
    singleton: true,

    /**
     * Запускает операцию тестирования правил качества
     *
     * @param isSandbox - признак тестирования существующих или тестовых записепй
     * @param selectedByIds - список идентафикаторов тестируемых записей
     * @param entityName - имя реестра / справочника в котором производится тестирование правил качества
     * @param sourceSystems - список систем источников данных
     */
    runDqTest: function (isSandbox, selectedByIds, entityName, dqRules) {
        var deferred = new Ext.Deferred(),
            mainUrl = Unidata.Config.getMainUrl(),
            tplUrl = '{0}internal/dq-sandbox/run',
            url = Ext.String.format(tplUrl, mainUrl);

        Ext.Ajax.unidataRequest({
            url: url,
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            jsonData: Ext.util.JSON.encode({
                selectedByIds: selectedByIds,
                entityName: entityName,
                sandbox: Boolean(isSandbox),
                rules: dqRules
            }),
            success: function (response) {
                var responseJson;

                if (response) {
                    responseJson = Ext.JSON.decode(response.responseText, true);
                }

                if (responseJson && responseJson.success) {
                    deferred.resolve(responseJson.content);
                } else {
                    deferred.reject();
                }
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    },

    /**
     * Возвращает тестовый dataRecord
     *
     * @param etalonId
     */
    getSandboxDataRecord: function (etalonId) {
        var deferred = new Ext.Deferred(),
            mainUrl = Unidata.Config.getMainUrl(),
            tplUrl = '{0}internal/dq-sandbox/record',
            url = Ext.String.format(tplUrl, mainUrl),
            promise;

        promise = Unidata.util.api.DataRecord.getDataRecord({
            etalonId: etalonId,
            url: url
        });

        promise.then(
            function (result) {
                deferred.resolve(result);
            },
            function (result) {
                deferred.reject(result);
            }
        ).done();

        return deferred.promise;
    },

    /**
     * Удаляет массово тестовые записи
     *
     * @param entityName
     * @param etalonIds
     */
    deleteSandboxDataRecords: function (entityName, etalonIds) {
        var deferred = new Ext.Deferred(),
            mainUrl = Unidata.Config.getMainUrl(),
            tplUrl = '{0}internal/dq-sandbox/record/delete',
            url = Ext.String.format(tplUrl, mainUrl);

        if (!etalonIds || !etalonIds.length) {
            etalonIds = null;
        }

        Ext.Ajax.unidataRequest({
            url: url,
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            jsonData: Ext.util.JSON.encode({
                ids: etalonIds,
                entityName: entityName
            }),
            success: function (response) {
                var responseJson;

                if (response) {
                    responseJson = Ext.JSON.decode(response.responseText, true);
                }

                if (responseJson && responseJson.success) {
                    deferred.resolve();
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

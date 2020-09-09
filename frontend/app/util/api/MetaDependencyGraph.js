/**
 * API получения MetaDependencyGraph
 *
 * @author Sergey Shishigin
 * @date 2016-11-01
 */

Ext.define('Unidata.util.api.MetaDependencyGraph', {
    extend: 'Unidata.util.api.AbstractApi',
    singleton: true,

    /**
     * Получить с сервера граф мета-зависимостей
     *
     * @param forTypes {String[]|null} Типы вершин, для которых строим граф
     * @param [skipTypes=[]] {String[]|null} Типы вершин, которые исключаем из выдачи
     * @param draft {String[]|null} Признак черновика
     */
    getMetaDependencyGraph: function (forTypes, skipTypes, draft) {
        var deferred = Ext.create('Ext.Deferred'),
            url = Unidata.Api.getMetaDependencyGraphUrl(draft),
            jsonData,
            params;

        forTypes = forTypes || [];
        skipTypes = skipTypes || [];

        params = {
            forTypes: forTypes,
            skipTypes: skipTypes
        };

        jsonData = Ext.util.JSON.encode(params);

        Ext.Ajax.request({
            method: 'POST',
            jsonData: jsonData,
            url: url,
            success: function (response) {
                deferred.resolve(this.retrieveMetaDependencyGraph(response));
            },
            failure: function () {
                deferred.reject();
            },
            scope: this
        });

        return deferred.promise;
    },

    /**
     * Получить граф мета-зависимости из response
     * @param response {Object}
     * @returns {Unidata.model.entity.metadependency.MetaDependencyGraph|null}
     */
    retrieveMetaDependencyGraph: function (response) {
        var responseJson,
            metaDependencyGraph = null,
            reader,
            resultSet;

        responseJson = Ext.util.JSON.decode(response.responseText, true);

        reader = Ext.create('Ext.data.JsonReader', {
            model: 'Unidata.model.entity.metadependency.MetaDependencyGraph'
        });

        resultSet = reader.readRecords(responseJson.content);

        if (resultSet && resultSet.count > 0) {
            metaDependencyGraph = resultSet.records[0];
        }

        return metaDependencyGraph;
    }
});

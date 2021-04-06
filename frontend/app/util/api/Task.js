/**
 * API загрузки relations digest
 */

Ext.define('Unidata.util.api.Task', {
    singleton: true,

    /**
     * Получить задачи
     *
     * @param cfg
     * store
     * values
     * variablesList
     * page
     *
     * @returns Ext.promise}
     */
    getTasks: function (cfg) {
        var variablesList = cfg.variablesList,
            values = cfg.values,
            store = cfg.store,
            page = cfg.page,
            proxy,
            variables = {},
            deferred;

        deferred = Ext.create('Ext.Deferred');
        page = Ext.isNumber(page) ? page : 1;

        if (!store) {
            store = this.createTaskSearchHitStore();
        }

        proxy = store.getProxy();

        Ext.Object.clear(proxy.getExtraParams());

        Ext.Object.each(values, function (key, value) {
            if (!value) {
                return;
            }

            if (Ext.Array.contains(variablesList, key)) {
                variables[key] = value;
            } else {
                proxy.setExtraParam(key, value);
            }
        });

        if (!Ext.Object.isEmpty(variables)) {
            proxy.setExtraParam('variables', variables);
        }

        store.on('load', function (store, records, successful) {
            if (!successful) {
                deferred.reject();
            } else {
                deferred.resolve(records);
            }
        }, this);

        store.loadPage(page);

        return deferred.promise;
    },

    /**
     * Запросить агрегированную информацию по задачам
     *
     * @param cfg
     * fromDate {Date} - дата последнего запроса (необходима для формирования числа новых задач)
     *
     * В ответ на запрос приходят следующие значения:
     * total_user_count - задачи в работе
     * available_count - общие задачи
     * new_count_from_date - новые задачи (относительно референтной даты fromDate)
     *
     * @returns {Ext.promise}
     */
    getTaskStat: function (cfg) {
        var url = Unidata.Api.getTaskStatUrl(),
            fromDate,
            deferred,
            params = {},
            dateFormat = Unidata.Config.getDateTimeFormatProxy();

        cfg = cfg || {};
        deferred = Ext.create('Ext.Deferred');
        fromDate = cfg.fromDate;

        if (fromDate) {
            params = {
                fromDate: Ext.Date.format(fromDate, dateFormat)
            };
        }

        Ext.Ajax.unidataRequest({
            method: 'GET',
            headers: {
                'PROLONG_TTL': 'false' // сервер не должен продлевать сессию
            },
            url: url,
            success: function (response) {
                var result;

                result = this.parseTaskCountResponse(response);

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

    parseTaskCountResponse: function (response) {
        var result,
            responseJson;

        responseJson = Ext.util.JSON.decode(response.responseText, true);
        result = responseJson.content;

        return result;
    },

    /**
     * Получить response object из read operation объекта
     *
     * @param {Ext.data.operation.Read} readOperation
     * @returns {Object}
     */
    getTasksResponse: function (readOperation) {
        var response,
            responseText,
            responseJson;

        response = readOperation.getResponse();
        responseText = response.responseText;
        responseJson = Ext.util.JSON.decode(responseText, true);

        return responseJson;
    },

    createTaskSearchHitStore: function () {
        var store;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.workflow.Task',
            proxy: 'workflow.task.search',
            pageSize: 10
        });

        return store;
    },

    getMyTasks: function (customCfg) {
        var cfg,
            user = Unidata.Config.getUser(),
            userName = user.get('login');

        cfg = {
            values: {
                assignedUser: userName
            },
            variablesList: ['entityName']
        };

        cfg = Ext.apply(cfg, customCfg);

        return this.getTasks(cfg);
    },

    getAvailableTasks: function (customCfg) {
        var cfg,
            user = Unidata.Config.getUser(),
            userName = user.get('login');

        cfg = {
            values: {
                candidateUser: userName
            },
            variablesList: ['entityName']
        };

        cfg = Ext.apply(cfg, customCfg);

        return this.getTasks(cfg);
    }
});

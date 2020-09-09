/**
 * API взаимодействия с job trigger
 *
 * @author Sergey Shishigin
 * @date 2017-12-19
 */

Ext.define('Unidata.util.api.JobTrigger', {
    singleton: true,

    /**
     * Сохранить job trigger
     * @param cfg
     * @returns {Ext.promise.Promise}
     */
    saveJobTrigger: function (cfg) {
        var deferred,
            jobId = cfg.jobId,
            jobTrigger = cfg.jobTrigger,
            jobTriggerId = jobTrigger.get('id'),
            url = Unidata.Api.getJobTriggerUrl(jobId, jobTriggerId),
            data,
            method;

        deferred = new Ext.Deferred();
        method = 'PUT';
        data = jobTrigger.getData();
        Ext.Ajax.request({
            url: url,
            method: method,
            headers: {'Content-Type': 'application/json'},
            jsonData: Ext.util.JSON.encode(data),
            success: function (response) {
                var responseJson;

                responseJson = Ext.util.JSON.decode(response.responseText, true);

                deferred.resolve(responseJson);
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    },

    /**
     * Удалить job trigger
     * @param cfg
     * @returns {Ext.promise.Promise}
     */
    deleteJobTrigger: function (cfg) {
        var deferred,
            jobId = cfg.jobId,
            jobTrigger = cfg.jobTrigger,
            triggerId = jobTrigger.get('id'),
            url = Unidata.Api.getJobTriggerUrl(jobId, triggerId),
            data;

        deferred = new Ext.Deferred();
        data = {};
        Ext.Ajax.request({
            url: url,
            method: 'DELETE',
            headers: {'Content-Type': 'application/json'},
            jsonData: Ext.util.JSON.encode(data),
            success: function () {
                deferred.resolve();
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    },

    /**
     * Загрузить job triggers
     * @param jobId
     * @returns {Ext.promise.Promise}
     */
    loadJobTriggers: function (jobId) {
        var url,
            deferred,
            store;

        url = Unidata.Api.getJobTriggerUrl(jobId);
        store = this.createJobTriggerStore({
            url: url
        });
        deferred = new Ext.Deferred();

        store.on('load', function (store, jobTriggers, successful) {
            if (!successful) {
                deferred.reject();
            } else {
                deferred.resolve(jobTriggers);
            }
        });
        store.load();

        return deferred.promise;
    },

    /**
     * Создать job trigger store
     * @param cfg
     * @returns {Ext.store.Store}
     */
    createJobTriggerStore: function (cfg) {
        var url = cfg.url,
            store;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.job.JobTrigger',
            proxy: {
                type: 'rest',
                url: url,
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        });

        return store;
    }
});

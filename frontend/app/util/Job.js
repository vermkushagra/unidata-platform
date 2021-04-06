/**
 * Сервисный класс для работы с операциями
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.util.Job', {

    requires: [
        'Unidata.model.job.execution.Execution'
    ],

    singleton: true,

    privates: {

        /**
         * Хедеры, необходимые для корректной работы REST API
         */
        AJAX_HEADERS: {
            'Accept':       'application/json',
            'Content-Type': 'application/json'
        },

        /**
         * Фабрика, которая возвращает функцию для обработки ошибок,
         * которые пришли вместе с response
         *
         * @param {String} msg - текст сообщения, который нужно показать в случае ошибки
         *
         * @private
         * @returns {Function}
         */
        getResponseChecker: function () {
            return function showResponseErrors (response) {

                var responseData = JSON.parse(response.responseText);

                if (responseData.errors && responseData.errors.length) {
                    Unidata.showError(responseData.errors[0].userMessage, false);

                    return true;
                }

                return false;

            };
        }

    },

    /**
     * Запуск операции
     *
     * @param {Unidata.model.job.Job} job
     * @param {Function} callback
     */
    startJob: function (job, callback) {

        var JobExecution = Unidata.model.job.execution.Execution,
            showResponseErrorsIfExists = this.getResponseChecker(Unidata.i18n.t('util>startOperationError'));

        if (!job.startAllowed()) {
            return;
        }

        job.setStatus(JobExecution.STATUS_WAIT);

        Ext.Ajax.request({
            url: Unidata.Api.getJobStartUrl() + '/' + job.get('id'),
            method: 'POST',
            headers: this.AJAX_HEADERS,
            success: function (response) {
                if (!showResponseErrorsIfExists(response)) {
                    job.setStatus(JobExecution.STATUS_STARTING);
                }
            },
            failure: function (response) {
                showResponseErrorsIfExists(response);
                job.setStatus(JobExecution.STATUS_STOPPED);
            },
            callback: callback || Ext.emptyFn
        });
    },

    /**
     * Остановка операции
     *
     * @param {Unidata.model.job.Job} job
     * @param {Function} callback
     */
    stopJob: function (job, callback) {

        var JobExecution = Unidata.model.job.execution.Execution,
            showResponseErrorsIfExists = this.getResponseChecker(Unidata.i18n.t('util>stopOperationError'));

        if (!job.stopAllowed()) {
            return;
        }

        job.setStatus(JobExecution.STATUS_WAIT);

        Ext.Ajax.request({
            url: Unidata.Api.getJobStopUrl() + '/' + job.get('id'),
            method: 'POST',
            headers: this.AJAX_HEADERS,
            success: function (response) {
                if (!showResponseErrorsIfExists(response)) {
                    job.setStatus(JobExecution.STATUS_STOPPED);
                }
            },
            failure: function (response) {
                showResponseErrorsIfExists(response);
            },
            callback: callback || Ext.emptyFn
        });
    },

    /**
     * Включение/выключение операции
     *
     * @param {Unidata.model.job.Job} job
     * @param {Boolean} enabled
     */
    markJob: function (job, enabled) {

        var showResponseErrorsIfExists = this.getResponseChecker(Unidata.i18n.t('util>startStopOperationError'));

        Ext.Ajax.request({
            url: Unidata.Api.getJobMarkUrl() + '/' + job.get('id') + '/' + (enabled ? 'true' : 'false'),
            method: 'POST',
            headers: this.AJAX_HEADERS,
            success: showResponseErrorsIfExists,
            failure: showResponseErrorsIfExists
        });
    },

    restartExecution: function (execution, callback) {

        Ext.Ajax.request({
            url: Unidata.Api.getRestartExecutionUrl() + '/' + execution.get('id'),
            method: 'POST',
            headers: this.AJAX_HEADERS,
            callback: callback || Ext.emptyFn
        });
    }

});

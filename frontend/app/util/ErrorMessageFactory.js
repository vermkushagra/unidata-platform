/**
 * Утилитный класс для порождения всплывающих ошибок
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.util.ErrorMessageFactory', {
    singleton: true,

    defaultErrorText: Unidata.i18n.t('util>unknownError'),

    severityList: ['LOW', 'NORMAL', 'HIGH', 'CRITICAL'],

    /**
     * Получить errors из соответствующей секции response
     * @param response
     * @returns {*}
     */
    getErrorsFromResponse: function (response) {
        var responseText = response.responseText;

        if (responseText) {
            response = Ext.decode(responseText, true);
        }

        return response && response.errors ? response.errors : [];
    },

    getErrorFromResponseByErrorCode: function (response, errorCode) {
        var errors,
            me = this,
            error = null;

        function findByErrorCode (errorCode, error) {
            return error.errorCode === errorCode;
        }

        errors = this.getErrorsFromResponse(response);
        error = Ext.Array.findBy(errors, findByErrorCode.bind(me, errorCode));

        return error;
    },

    isErrorShow: function (response) {
        var msg;

        msg  = Unidata.util.ErrorMessageFactory.getErrorUserMessagesFromResponse(response);

        return response.status === 200 && msg ? msg : false;
    },

    /**
     * Получить stackTrace из соответствующей секции response
     * @param response
     * @returns {*}
     */
    getStackTraceFromResponse: function (response) {
        var responseText = response.responseText;

        if (responseText) {
            response = Ext.decode(responseText, true);
        }

        return response && response.stackTrace ? response.stackTrace : null;
    },

    /**
     * Получить массив пользовательских сообщений из response
     *
     * @param response
     * @returns {*}
     */
    getErrorUserMessagesFromResponse: function (response) {
        var errors,
            userMessages;

        errors = this.getErrorsFromResponse(response);
        userMessages = Ext.Array.pluck(errors, 'userMessage');
        //userMessages = Ext.Array.filter(userMessages, function (userMessage) {
        //    return userMessage;
        //});

        return userMessages.length > 0 ? userMessages : null;
    },

    /**
     * Получить массив расширенных пользовательских сообщений из response
     *
     * @param response
     */
    getUserMessageDetailsFromResponse: function (response) {
        var errors,
            userMessageDetails;

        errors = this.getErrorsFromResponse(response);
        userMessageDetails = Ext.Array.pluck(errors, 'userMessageDetails');
        userMessageDetails = Ext.Array.filter(userMessageDetails, function (userMessageDetail) {
            return userMessageDetail !== null;
        });

        return userMessageDetails.length > 0 ? userMessageDetails : null;
    },

    /**
     * Получить severity из секции errors response
     *
     * Severity максимальный для всех сообщений
     * @param response
     * @returns {string}
     */
    getMaxSeverityFromResponse: function (response) {
        var errors,
            severities,
            severityPoints,
            maxSeverity = 'NORMAL',
            maxSeverityIndex,
            me = this;

        errors = this.getErrorsFromResponse(response);
        severities = Ext.Array.pluck(errors, 'severity');

        severityPoints = Ext.Array.map(severities, function (severity) {
            return Ext.Array.indexOf(me.severityList, severity);
        });

        maxSeverityIndex = Math.max.apply(null, severityPoints);

        if (maxSeverityIndex > -1) {
            maxSeverity = this.severityList[maxSeverityIndex];
        }

        return maxSeverity;
    },

    /**
     * Преобразовать kvp to object
     * @param params
     * @returns {{}}
     */
    mapToObject: function (params) {
        var obj = {};

        params.forEach(function (item) {
            obj[item.get('field')] = item.get('value');
        });

        return obj;
    }
});
